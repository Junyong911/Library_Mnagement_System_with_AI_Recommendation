package com.example.test;

public class Book {
    private String bookId;
    private String title;
    private String genre;
    private String author;
    private String isbn;
    private String description;
    private String imageUrl;
    private String barcode;
    private String status;
    private float rating; // Average rating of the book
    private int ratingsCount; // Total number of ratings

    // Default constructor (required for Firebase)
    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    // Constructor with new fields for rating
    public Book(String bookId, String title, String genre, String author, String isbn, String description, String imageUrl, String barcode, String status, float rating, int ratingsCount) {
        this.bookId = bookId;
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.imageUrl = imageUrl;
        this.barcode = barcode;
        this.status = status;
        this.rating = rating;
        this.ratingsCount = ratingsCount;
    }

    // Getters and Setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }
}
