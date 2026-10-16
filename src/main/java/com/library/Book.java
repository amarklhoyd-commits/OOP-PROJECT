package com.library;

/**
 * Inheritance: Book is a specific kind of LibraryItem.
 */
public class Book extends LibraryItem {
    public Book(int id, String title, String author, boolean available) {
        super(id, title, author, available);
    }

    @Override
    public int getBorrowingPeriod() { return 14; }

    @Override
    public String getItemType() { return "REGULAR"; }
}
