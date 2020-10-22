/*
 * User
 *
 * October 21, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.models;

/**
 * Represents a user in our application domain. This user information is stored in Firestore and is
 * related to a user entry in Firebase.
 * <p>
 * TODO: Link with Firebase
 * TODO: Add authorization to secure allowed methods (e.g. editing user data)
 */
public class User {
    private String mId;
    private String mUsername;
    private String mEmail;

    /**
     * Empty constructor. Needed for Firestore.
     */
    public User() {
    }

    /**
     * Constructs a user.
     *
     * @param id       unique ID associated with the user, obtained from Firebase Auth
     * @param username that uniquely represents the user other than its ID
     * @param email    of the user
     */
    public User(String id, String username, String email) {
        mId = id;
        mUsername = username;
        mEmail = email;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return unique user id associated with the user
     */
    public String getId() {
        return mId;
    }

    /**
     * Gets the user's username.
     *
     * @return username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Updates the current username. Can only be done to the current user.
     *
     * @param username updated username
     */
    public void setUsername(String username) {
        mUsername = username;
    }

    /**
     * Gets the user's email.
     *
     * @return email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Updates the current user's email. Can only be done to the current user.
     *
     * @param email updated email
     */
    public void setEmail(String email) {
        mEmail = email;
    }
}
