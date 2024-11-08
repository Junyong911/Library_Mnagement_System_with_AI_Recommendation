package com.example.test;

public class UserBorrowBookClass {
    private String borrowId;
    private String bookId;
    private String userId;
    private long borrowTimestamp;
    private String bookTitle;
    private String bookAuthor;
    private String bookRating;
    private String bookReviewsCount;
    private String bookCoverUrl;
    private String bookGenre;  // Add genre field

    public UserBorrowBookClass(String borrowId, String bookId, String userId, long borrowTimestamp,
                               String bookTitle, String bookAuthor, String bookRating,
                               String bookReviewsCount, String bookCoverUrl, String bookGenre) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowTimestamp = borrowTimestamp;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookRating = bookRating;
        this.bookReviewsCount = bookReviewsCount;
        this.bookCoverUrl = bookCoverUrl;
        this.bookGenre = bookGenre;  // Set the genre
    }

    // Getters and Setters for the fields including bookGenre
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