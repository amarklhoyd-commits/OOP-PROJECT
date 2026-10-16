package com.library;

import java.time.LocalDate;

public class BorrowRecord {
    private final int id;
    private final int bookId;
    private final int memberId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;

    public BorrowRecord(int id, int bookId, int memberId, LocalDate borrowDate,
                        LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returnDate != null; }
}
