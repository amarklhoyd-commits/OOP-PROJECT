package com.library;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Application/service layer: database operations are kept out of the GUI.
 */
public class LibraryManager {
    public List<LibraryItem> getBooks(String search) throws SQLException {
        String sql = "SELECT id,title,author,category,available FROM books "
                   + "WHERE title LIKE ? OR author LIKE ? ORDER BY id";
        List<LibraryItem> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String term = "%" + (search == null ? "" : search.trim()) + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    boolean available = rs.getBoolean("available");
                    String category = rs.getString("category");
                    LibraryItem item = "REFERENCE".equalsIgnoreCase(category)
                            ? new ReferenceBook(id, title, author, available)
                            : new Book(id, title, author, available);
                    result.add(item);
                }
            }
        }
        return result;
    }

    public void addBook(String title, String author, String category) throws SQLException {
        validateText(title, "Title");
        validateText(author, "Author");
        if (!"REGULAR".equals(category) && !"REFERENCE".equals(category))
            throw new IllegalArgumentException("Choose a valid book category.");
        String sql = "INSERT INTO books(title,author,category,available) VALUES(?,?,?,TRUE)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title.trim());
            ps.setString(2, author.trim());
            ps.setString(3, category);
            ps.executeUpdate();
        }
    }

    public void updateBook(int id, String title, String author, String category) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Select a book first.");
        validateText(title, "Title");
        validateText(author, "Author");
        if (!"REGULAR".equals(category) && !"REFERENCE".equals(category))
            throw new IllegalArgumentException("Choose a valid book category.");
        String sql = "UPDATE books SET title=?,author=?,category=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title.trim());
            ps.setString(2, author.trim());
            ps.setString(3, category);
            ps.setInt(4, id);
            if (ps.executeUpdate() == 0) throw new IllegalArgumentException("Book was not found.");
        }
    }

    public void deleteBook(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Select a book first.");
        String check = "SELECT available FROM books WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(check)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("Book was not found.");
                if (!rs.getBoolean("available")) throw new IllegalArgumentException("Return this book before deleting it.");
            }
            try (PreparedStatement del = c.prepareStatement("DELETE FROM books WHERE id=?")) {
                del.setInt(1, id);
                del.executeUpdate();
            }
        }
    }

    public List<Member> getMembers(String search) throws SQLException {
        List<Member> result = new ArrayList<>();
        String sql = "SELECT id,full_name,email FROM members WHERE full_name LIKE ? OR email LIKE ? ORDER BY id";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String term = "%" + (search == null ? "" : search.trim()) + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(new Member(rs.getInt("id"), rs.getString("full_name"), rs.getString("email")));
            }
        }
        return result;
    }

    public void addMember(String name, String email) throws SQLException {
        Member member = new Member(0, name, email); // validates fields
        String sql = "INSERT INTO members(full_name,email) VALUES(?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, member.getFullName());
            ps.setString(2, member.getEmail());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("That email address is already registered.");
        }
    }

    public void borrowBook(int bookId, int memberId) throws SQLException {
        if (bookId <= 0 || memberId <= 0) throw new IllegalArgumentException("Enter valid book and member IDs.");
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                String sql = "SELECT id,title,author,category,available FROM books WHERE id=? FOR UPDATE";
                LibraryItem item;
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Book ID was not found.");
                        if (!rs.getBoolean("available")) throw new IllegalArgumentException("This book is already borrowed.");
                        String category = rs.getString("category");
                        item = "REFERENCE".equalsIgnoreCase(category)
                                ? new ReferenceBook(rs.getInt("id"), rs.getString("title"), rs.getString("author"), true)
                                : new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), true);
                    }
                }
                if (item.getBorrowingPeriod() == 0)
                    throw new IllegalArgumentException("Reference books cannot be borrowed.");
                try (PreparedStatement ps = c.prepareStatement("SELECT id FROM members WHERE id=?")) {
                    ps.setInt(1, memberId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Member ID was not found.");
                    }
                }
                LocalDate today = LocalDate.now();
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO borrow_records(book_id,member_id,borrow_date,due_date) VALUES(?,?,?,?)")) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, memberId);
                    ps.setDate(3, Date.valueOf(today));
                    ps.setDate(4, Date.valueOf(today.plusDays(item.getBorrowingPeriod())));
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE books SET available=FALSE WHERE id=?")) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                if (e instanceof SQLException sqlEx) throw sqlEx;
                if (e instanceof RuntimeException runtimeEx) throw runtimeEx;
                throw new SQLException(e);
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void returnBook(int bookId) throws SQLException {
        if (bookId <= 0) throw new IllegalArgumentException("Enter a valid book ID.");
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int recordId;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT id FROM borrow_records WHERE book_id=? AND return_date IS NULL ORDER BY id DESC LIMIT 1 FOR UPDATE")) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("No active borrowing record found for this book.");
                        recordId = rs.getInt("id");
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE borrow_records SET return_date=? WHERE id=?")) {
                    ps.setDate(1, Date.valueOf(LocalDate.now()));
                    ps.setInt(2, recordId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE books SET available=TRUE WHERE id=?")) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                if (e instanceof SQLException sqlEx) throw sqlEx;
                if (e instanceof RuntimeException runtimeEx) throw runtimeEx;
                throw new SQLException(e);
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public List<BorrowRecord> getBorrowRecords() throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT id,book_id,member_id,borrow_date,due_date,return_date FROM borrow_records ORDER BY id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date returned = rs.getDate("return_date");
                records.add(new BorrowRecord(rs.getInt("id"), rs.getInt("book_id"), rs.getInt("member_id"),
                        rs.getDate("borrow_date").toLocalDate(), rs.getDate("due_date").toLocalDate(),
                        returned == null ? null : returned.toLocalDate()));
            }
        }
        return records;
    }

    private void validateText(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        if (value.trim().length() > 150) throw new IllegalArgumentException(field + " must be 150 characters or fewer.");
    }
}
