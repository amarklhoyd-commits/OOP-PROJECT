package com.library;

public class Member {
    private int id;
    private String fullName;
    private String email;

    public Member(int id, String fullName, String email) {
        setId(id);
        setFullName(fullName);
        setEmail(email);
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }

    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("Member ID cannot be negative.");
        this.id = id;
    }
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) throw new IllegalArgumentException("Member name is required.");
        this.fullName = fullName.trim();
    }
    public void setEmail(String email) {
        if (email == null || !email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        this.email = email.trim();
    }
}
