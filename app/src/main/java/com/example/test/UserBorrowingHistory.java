package com.example.test;

public class UserBorrowingHistory {

    private String borrowId;
    private String bookId;
    private long borrowTimestamp;

    // Default constructor required for Firebase
    public UserBorrowingHistory() {
    }

    public UserBorrowingHistory(String borrowId, String bookId, long borrowTimestamp) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.borrowTimestamp = borrowTimestamp;
    }

    // Getters and Setters
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

    public long getBorrowTimestamp() {
        return borrowTimestamp;
    }

    public void setBorrowTimestamp(long borrowTimestamp) {
        this.borrowTimestamp = borrowTimestamp;
    }
}
