/*
 * Request
 *
 * October 21, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.models;

import androidx.core.util.Pair;

import com.google.firebase.firestore.DocumentId;

/**
 * Represents a borrow request in our application domain.
 * <p>
 */
public class Request {
    private String mId;
    private String mRequester;
    private String mBook;
    private State mState;
    private Pair<Double, Double> mLocation; // TODO: Check whether this can be stored in Firestore

    /**
     * Empty constructor. Needed for Firestore.
     */
    public Request() {
    }

    /**
     * Constructs a request for a book.
     *
     * @param requester reference to a user unique id
     * @param book      reference to the book's unique id
     */
    public Request(String requester, String book) {
        mRequester = requester;
        mBook = book;
        mState = State.REQUESTED;
    }

    /**
     * Returns the unique identifier of a request obtained from Firestore.
     *
     * @return unique identifier of request
     */
    @DocumentId
    public String getId() {
        return mId;
    }

    /**
     * Sets the unique identifier of a request obtained from Firestore.
     */
    public void setId(String id) {
        if (mId != null) {
            throw new IllegalArgumentException("ID has already been initialized");
        }

        mId = id;
    }

    /**
     * Gets the unique borrower user ID who initiated the request.
     *
     * @return unique ID of borrower that initiated the request
     */
    public String getRequester() {
        return mRequester;
    }

    /**
     * Sets the unique borrower user ID who initiated the request
     *
     * @param requester unique identifier of a user
     */
    public void setRequester(String requester) {
        if (mRequester != null) {
            throw new IllegalArgumentException("Requester has already been initialized");
        }
        mRequester = requester;
    }

    /**
     * Gets the unique ID of the book to be borrowed.
     *
     * @return unique ID of book that is involved with the request
     */
    public String getBook() {
        return mBook;
    }

    /**
     * Sets the unique ID of the book to be borrowed.
     *
     * @param book unique identifier of a book
     */
    public void setBook(String book) {
        if (mBook != null) {
            throw new IllegalArgumentException("Book has already been initialized");
        }
        mBook = book;
    }

    /**
     * Gets the state of a request.
     * <p>
     * REQUESTED - Borrower initiates a request to borrow a book, may be accepted/declined by the owner
     * ACCEPTED - Owner has accepted to lend the book to a borrower, other borrowers are moved to declined
     * BORROWED - Owner has handed off the book to the borrower
     * ARCHIVED - Request has completed either by returning the book to the owner or request was declined
     *
     * @return one of the above state
     */
    public State getState() {
        return mState;
    }

    /**
     * Updates the state of the request.
     * <p>
     * REQUESTED - Borrower initiates a request to borrow a book, may be accepted/declined by the owner
     * ACCEPTED - Owner has accepted to lend the book to a borrower, other borrowers are moved to declined
     * BORROWED - Owner has handed off the book to the borrower
     * ARCHIVED - Request has completed either by returning the book to the owner or request was declined
     *
     * @param state one of the states above
     */
    public void setState(State state) {
        mState = state;
    }

    /**
     * Gets the handoff location described in latitude-longitude pair.
     *
     * @return latitude-longitude pair of handoff location.
     */
    public Pair<Double, Double> getLocation() {
        return mLocation;
    }

    /**
     * Sets the handoff location described in latitude-longitude pair.
     * Latitude-longitude should be retrieved from google maps API.
     *
     * @param location described in latitude-longitude
     */
    public void setLocation(Pair<Double, Double> location) {
        mLocation = location;
    }

    public enum State {
        REQUESTED,
        ACCEPTED,
        BORROWED,
        ARCHIVED
    }
}
