package com.library;

/**
 * Another LibraryItem type. Polymorphism allows a different borrowing policy.
 */
public class ReferenceBook extends LibraryItem {
    public ReferenceBook(int id, String title, String author, boolean available) {
        super(id, title, author, available);
    }

    @Override
    public int getBorrowingPeriod() { return 0; }

    @Override
    public String getItemType() { return "REFERENCE"; }
}
