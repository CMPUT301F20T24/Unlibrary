package com.example.unlibrary.models;

import java.util.ArrayList;

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

    public Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    ArrayList<String> photos;
    Status status;

    public String getIsbn() {
        return this.isbn;
    }

    public String getTitle() {
        return this.title;
    }
}
