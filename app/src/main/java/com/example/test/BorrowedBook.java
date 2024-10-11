package com.example.test;

public class BorrowedBook {
    private String title;
    private String author;
    private String genre;
    private String coverUrl;
    private float rating;

    public BorrowedBook() { }

    public BorrowedBook(String title, String author, String genre, String coverUrl, float rating) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.coverUrl = coverUrl;
        this.rating = rating;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
