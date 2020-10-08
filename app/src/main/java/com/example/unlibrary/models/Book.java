package com.example.unlibrary.models;

public class Book {
    enum Status {
        AVAILABLE,
        REQUESTED,
        ACCEPTED,
        BORROWED
    }

    String isbn;
    String description;
    String title;
    String author;
    ArrayList<String> photos;
    Status status;
}
