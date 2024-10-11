package com.example.test;

public class UserBorrowBookClass {
    private String borrowId;
    private String bookId;
    private String userId;
    private long borrowTimestamp;

    public UserBorrowBookClass() {
        // Default constructor required for Firebase
    }

    public UserBorrowBookClass(String borrowId, String bookId, String userId, long borrowTimestamp) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowTimestamp = borrowTimestamp;
    }

    // Getters and setters
    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getBorrowTimestamp() {
        return borrowTimestamp;
    }

    public void setBorrowTimestamp(long borrowTimestamp) {
        this.borrowTimestamp = borrowTimestamp;
    }
}