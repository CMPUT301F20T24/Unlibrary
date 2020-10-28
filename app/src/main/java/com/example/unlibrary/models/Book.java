/*
 * Book
 *
 * October 21, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.models;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;

/**
 * Represents a book in our application domain. Book is a pure POJO class (getters and setters only)
 * so that it can be used as a custom object when interacting with Firestore.
 * <p>
 */
public class Book {

    private String mId;
    private String mIsbn;
    private String mTitle;
    private String mAuthor;
    private String mOwner;
    private ArrayList<String> mPhotos;
    private Status mStatus;

    /**
     * Empty constructor. Needed for Firestore.
     * Should not be used in code manually.
     */
    public Book() {
    }

    /**
     * Constructor a book for cardviews
     * <p>
     * TODO: delete this constructor once determined how to store images taken by the camera
     * TODO: link logged in user with the owner of books to modify the book lists in each fragment
     *
     * @param isbn
     * @param mTitle
     * @param mAuthor
     * @param mStatus
     */

    public Book(String isbn, String mTitle, String mAuthor, String mStatus) {
        this.mIsbn = isbn;
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mStatus = Status.valueOf(mStatus);
    }

    /**
     * Construct a book and initialize it with a list of photos.
     *
     * @param isbn   of book
     * @param title  of book
     * @param author of book
     * @param owner  unique user identifier of the owner of the book
     * @param photos URL of photos to add, can be null if no photos are added
     */
    public Book(String isbn, String title, String author, String owner, ArrayList<String> photos) {
        mIsbn = isbn;
        mTitle = title;
        mAuthor = author;
        mOwner = owner;
        mStatus = Status.AVAILABLE;

        if (photos != null) {
            mPhotos = photos;
        } else {
            mPhotos = new ArrayList<>();
        }
    }

    /**
     * Gets the unique identifier of the book. Retrieved from Firestore.
     *
     * @return book's unique identifier
     */
    @DocumentId
    public String getId() {
        return mId;
    }

    /**
     * Updates the unique identifier of the book. Should not be called explicitly in code. This is
     * called automatically when {@link com.google.firebase.firestore.DocumentSnapshot#toObject(Class)}
     * is called when retrieving documents from Firestore.
     *
     * @param id updated unique identifier of book
     */
    public void setId(String id) {
        if (mId != null) {
            throw new IllegalArgumentException("ID has already been initialized");
        }

        mId = id;
    }

    /**
     * Gets the author of a book.
     *
     * @return author of current book
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Updates the author of the book. Can only be done by the book owner.
     *
     * @param author updated name of author
     */
    public void setAuthor(String author) {
        mAuthor = author;
    }

    /**
     * Gets the list of photo URLs associated with this book.
     *
     * @return list of photo URLs that can be fetched
     */
    public ArrayList<String> getPhotos() {
        return mPhotos;
    }

    /**
     * Updates the list of photo URLs associated with this book. Can only be done by the book owner.
     *
     * @param photos updated list of photo URLs
     */
    public void setPhotos(ArrayList<String> photos) {
        mPhotos = photos;
    }

    /**
     * Returns the current status of the book.
     * <p>
     * AVAILABLE - Book has no requests from anyone
     * REQUESTED - Book has at least one request from someone but may not be current user
     * ACCEPTED - Book is to be handed off but may not be to the current user
     * BORROWED - Book is in borrowers hand but may not be current user
     *
     * @return one of the states above
     */
    public Status getStatus() {
        return mStatus;
    }

    /**
     * Updates the state of the book.
     * <p>
     * AVAILABLE - Book has no requests from anyone
     * REQUESTED - Book has at least one request from someone but may not be current user
     * ACCEPTED - Book is to be handed off but may not be to the current user
     * BORROWED - Book is in borrowers hand but may not be current user
     *
     * @param status updated state
     */
    public void setStatus(Status status) {
        mStatus = status;
    }

    /**
     * Gets the isbn of the book.
     *
     * @return book isbn
     */
    public String getIsbn() {
        return mIsbn;
    }

    /**
     * Updates the isbn of the book.
     *
     * @param isbn updated isbn
     */
    public void setIsbn(String isbn) {
        mIsbn = isbn;
    }

    /**
     * Gets the unique user ID of the owner of the book
     *
     * @return unique user ID of the book's owner
     */
    public String getOwner() {
        return mOwner;
    }

    /**
     * Updates the owner of the book
     *
     * @param owner unique id associated with the owner
     */
    public void setOwner(String owner) {
        mOwner = owner;
    }

    /**
     * Gets the title of the book. Multiple books may have the same title.
     *
     * @return title of the book
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Updates the title of the book
     *
     * @param title updated title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    // TODO: Check if there's a better way of holding this information without making another fetch request to the database to retrieve Requests
    public enum Status {
        AVAILABLE,
        REQUESTED,
        ACCEPTED,
        BORROWED
    }
}
