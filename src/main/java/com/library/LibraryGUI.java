package com.library;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class LibraryGUI extends JFrame {

    private final LibraryManager manager = new LibraryManager();

    private final DefaultTableModel bookModel = model("ID", "Title", "Author", "Category", "Availability");
    private final JTable bookTable = new JTable(bookModel);
    private final JTextField titleField = new JTextField(18);
    private final JTextField authorField = new JTextField(18);
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"REGULAR", "REFERENCE"});
    private final JTextField bookSearch = new JTextField(14);

    private final DefaultTableModel memberModel = model("ID", "Full Name", "Email");
    private final JTable memberTable = new JTable(memberModel);
    private final JTextField memberNameField = new JTextField(16);
    private final JTextField memberEmailField = new JTextField(16);
    private final JTextField memberSearch = new JTextField(12);

    private final DefaultTableModel recordModel = model("Record ID", "Book ID", "Member ID", "Borrow Date", "Due Date", "Return Date");
    private final JTable recordTable = new JTable(recordModel);
    private final JTextField borrowBookId = new JTextField(6);
    private final JTextField borrowMemberId = new JTextField(6);
    private final JTextField returnBookId = new JTextField(6);

    public LibraryGUI() {
        super("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("LIBRARY MANAGEMENT SYSTEM", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 23));
        heading.setBorder(BorderFactory.createEmptyBorder(16, 8, 8, 8));
        add(heading, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        tabs.addTab("Borrow / Return", createTransactionsPanel());
        add(tabs, BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel createBooksPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 6));
        form.setBorder(BorderFactory.createTitledBorder("Book details"));
        form.add(new JLabel("Title"));
        form.add(new JLabel("Author"));
        form.add(new JLabel("Category"));
        form.add(new JLabel("Search title/author"));
        form.add(titleField);
        form.add(authorField);
        form.add(categoryBox);
        form.add(bookSearch);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add Book");
        JButton update = new JButton("Update Selected");
        JButton delete = new JButton("Delete Selected");
        JButton search = new JButton("Search");
        JButton showAll = new JButton("Show All");
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(search);
        buttons.add(showAll);

        add.addActionListener(e -> runAction(() -> {
            manager.addBook(titleField.getText(), authorField.getText(), (String) categoryBox.getSelectedItem());
            clearBookFields();
            refreshBooks("");
            info("Book added successfully.");
        }));
        update.addActionListener(e -> runAction(() -> {
            int id = selectedBookId();
            manager.updateBook(id, titleField.getText(), authorField.getText(), (String) categoryBox.getSelectedItem());
            refreshBooks("");
            info("Book updated successfully.");
        }));
        delete.addActionListener(e -> runAction(() -> {
            int id = selectedBookId();
            if (JOptionPane.showConfirmDialog(this, "Delete selected book?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                manager.deleteBook(id);
                refreshBooks("");
                info("Book deleted.");
            }
        }));
        search.addActionListener(e -> runAction(() -> refreshBooks(bookSearch.getText())));
        showAll.addActionListener(e -> runAction(() -> {
            bookSearch.setText("");
            refreshBooks("");
        }));

        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() >= 0) {
                int row = bookTable.convertRowIndexToModel(bookTable.getSelectedRow());
                titleField.setText(String.valueOf(bookModel.getValueAt(row, 1)));
                authorField.setText(String.valueOf(bookModel.getValueAt(row, 2)));
                categoryBox.setSelectedItem(String.valueOf(bookModel.getValueAt(row, 3)));
            }
        });

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        outer.add(top, BorderLayout.NORTH);
        outer.add(new JScrollPane(bookTable), BorderLayout.CENTER);
        return outer;
    }

    private JPanel createMembersPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 8));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Register member"));
        form.add(new JLabel("Full name:"));
        form.add(memberNameField);
        form.add(new JLabel("Email:"));
        form.add(memberEmailField);
        JButton add = new JButton("Register Member");
        JButton search = new JButton("Search");
        JButton all = new JButton("Show All");
        form.add(add);
        form.add(search);
        form.add(all);
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Search name/email:"));
        searchRow.add(memberSearch);
        add.addActionListener(e -> runAction(() -> {
            manager.addMember(memberNameField.getText(), memberEmailField.getText());
            memberNameField.setText("");
            memberEmailField.setText("");
            refreshMembers("");
            info("Member registered successfully.");
        }));
        search.addActionListener(e -> runAction(() -> refreshMembers(memberSearch.getText())));
        all.addActionListener(e -> runAction(() -> {
            memberSearch.setText("");
            refreshMembers("");
        }));
        outer.add(form, BorderLayout.NORTH);
        outer.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        outer.add(searchRow, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel createTransactionsPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 8));
        JPanel actions = new JPanel(new GridLayout(2, 1, 8, 8));
        JPanel borrow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        borrow.setBorder(BorderFactory.createTitledBorder("Borrow a regular book"));
        borrow.add(new JLabel("Book ID:"));
        borrow.add(borrowBookId);
        borrow.add(new JLabel("Member ID:"));
        borrow.add(borrowMemberId);
        JButton borrowButton = new JButton("Borrow");
        borrow.add(borrowButton);
        borrowButton.addActionListener(e -> runAction(() -> {
            manager.borrowBook(parsePositiveInt(borrowBookId.getText(), "Book ID"),
                    parsePositiveInt(borrowMemberId.getText(), "Member ID"));
            refreshAll();
            info("Book borrowed. Standard due date is 14 days from today.");
        }));

        JPanel ret = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ret.setBorder(BorderFactory.createTitledBorder("Return a book"));
        ret.add(new JLabel("Book ID:"));
        ret.add(returnBookId);
        JButton returnButton = new JButton("Return");
        JButton refresh = new JButton("Refresh Records");
        ret.add(returnButton);
        ret.add(refresh);
        returnButton.addActionListener(e -> runAction(() -> {
            manager.returnBook(parsePositiveInt(returnBookId.getText(), "Book ID"));
            refreshAll();
            info("Book returned successfully.");
        }));
        refresh.addActionListener(e -> runAction(this::refreshRecords));
        actions.add(borrow);
        actions.add(ret);
        outer.add(actions, BorderLayout.NORTH);
        outer.add(new JScrollPane(recordTable), BorderLayout.CENTER);
        return outer;
    }

    private void refreshAll() {
        runAction(() -> {
            refreshBooks("");
            refreshMembers("");
            refreshRecords();
        });
    }

    private void refreshBooks(String search) throws SQLException {
        bookModel.setRowCount(0);
        for (LibraryItem item : manager.getBooks(search)) {
            bookModel.addRow(new Object[]{item.getId(), item.getTitle(), item.getAuthor(),
                item.getItemType(), item.isAvailable() ? "Available" : "Borrowed"});
        }
    }

    private void refreshMembers(String search) throws SQLException {
        memberModel.setRowCount(0);
        for (Member member : manager.getMembers(search)) {
            memberModel.addRow(new Object[]{member.getId(), member.getFullName(), member.getEmail()});
        }
    }

    private void refreshRecords() throws SQLException {
        recordModel.setRowCount(0);
        for (BorrowRecord r : manager.getBorrowRecords()) {
            recordModel.addRow(new Object[]{r.getId(), r.getBookId(), r.getMemberId(),
                r.getBorrowDate(), r.getDueDate(), r.getReturnDate() == null ? "Not returned" : r.getReturnDate()});
        }
    }

    private int selectedBookId() {
        int viewRow = bookTable.getSelectedRow();
        if (viewRow < 0) {
            throw new IllegalArgumentException("Select a book in the table first.");
        }
        int row = bookTable.convertRowIndexToModel(viewRow);
        return Integer.parseInt(String.valueOf(bookModel.getValueAt(row, 0)));
    }

    private int parsePositiveInt(String value, String field) {
        try {
            int n = Integer.parseInt(value.trim());
            if (n <= 0) {
                throw new NumberFormatException();
            }
            return n;
        } catch (Exception e) {
            throw new IllegalArgumentException(field + " must be a positive whole number.");
        }
    }

    private void runAction(SqlAction action) {
        try {
            action.run();
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage()
                    + "\nCheck that MySQL is running, database.sql was executed, and DatabaseConnection.java is configured.");
        } catch (Exception ex) {
            error("Unexpected error: " + ex.getMessage());
        }
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Unable to complete", JOptionPane.ERROR_MESSAGE);
    }

    private void clearBookFields() {
        titleField.setText("");
        authorField.setText("");
        categoryBox.setSelectedIndex(0);
    }

    private static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    @FunctionalInterface
    private interface SqlAction {

        void run() throws Exception;
    }


    public static void main(String[] args) {
    System.out.println("Starting Library Management System...");

    SwingUtilities.invokeLater(() -> {
        try {
            LibraryGUI gui = new LibraryGUI();
            System.out.println("GUI created successfully!");

            gui.setVisible(true);
            System.out.println("GUI displayed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}
}    