package com.library;

/**
 * Abstraction: defines the common state and behavior of library items.
 */
public abstract class LibraryItem {
    private int id;
    private String title;
    private String author;
    private boolean available;

    protected LibraryItem(int id, String title, String author, boolean available) {
        setId(id);
        setTitle(title);
        setAuthor(author);
        this.available = available;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }

    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("ID cannot be negative.");
        this.id = id;
    }
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title is required.");
        this.title = title.trim();
    }
    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) throw new IllegalArgumentException("Author is required.");
        this.author = author.trim();
    }
    public void setAvailable(boolean available) { this.available = available; }

    public abstract int getBorrowingPeriod();
    public abstract String getItemType();
}
