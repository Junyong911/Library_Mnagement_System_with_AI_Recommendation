package com.example.test;

public class UserBorrowingHistory {
    private String borrowId;
    private String bookId;
    private long borrowTimestamp;
    private String bookTitle;
    private String bookAuthor;
    private String bookRating;
    private String bookReviewsCount;
    private String bookCoverUrl;
    private String bookGenre;

    // Default constructor required for Firebase
    public UserBorrowingHistory() {
    }

    public UserBorrowingHistory(String borrowId, String bookId, long borrowTimestamp,
                                String bookTitle, String bookAuthor, String bookRating,
                                String bookReviewsCount, String bookCoverUrl, String bookGenre) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.borrowTimestamp = borrowTimestamp;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookRating = bookRating;
        this.bookReviewsCount = bookReviewsCount;
        this.bookCoverUrl = bookCoverUrl;
        this.bookGenre = bookGenre;
    }

    public String getBookGenre() {
        return bookGenre;
    }

    public void setBookGenre(String bookGenre) {
        this.bookGenre = bookGenre;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookRating() {
        return bookRating;
    }

    public void setBookRating(String bookRating) {
        this.bookRating = bookRating;
    }

    public String getBookReviewsCount() {
        return bookReviewsCount;
    }

    public void setBookReviewsCount(String bookReviewsCount) {
        this.bookReviewsCount = bookReviewsCount;
    }

    public String getBookCoverUrl() {
        return bookCoverUrl;
    }

    public void setBookCoverUrl(String bookCoverUrl) {
        this.bookCoverUrl = bookCoverUrl;
    }

    // Existing getters and setters...

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
