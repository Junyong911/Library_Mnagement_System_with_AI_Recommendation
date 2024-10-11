package com.example.test;

import java.util.List;

public class BookResponse {
    public String title;
    public List<Author> authors;
    public String description;
    public Cover cover;
    //public List<String> subjects;

    public static class Author {
        public String name;
    }

    public static class Cover {
        public String large;
    }
}