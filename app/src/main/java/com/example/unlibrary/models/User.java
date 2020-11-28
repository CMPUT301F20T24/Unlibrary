/*
 * User
 *
 * October 21, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.models;

import com.google.firebase.firestore.DocumentId;

/**
 * Represents a user in our application domain. This user information is stored in Firestore and is
 * related to a user entry in Firebase.
 * <p>
 */
public class User {
    private String mUID;
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
        mUID = id;
        mUsername = username;
        mEmail = email;
    }

    /**
     * Gets the unique identifier of the user. Retrieved from Firestore.
     *
     * @return unique user id associated with the user
     */
    @DocumentId
    public String getUID() {
        return mUID;
    }

    /**
     * Updates the unique identifier of the user. Should not be called explicitly in code. This is
     * called automatically when {@link com.google.firebase.firestore.DocumentSnapshot#toObject(Class)}
     * is called when retrieving documents from Firestore.
     *
     * @param id unique identifier of user
     */
    public void setUID(String id) {
        if (mUID != null) {
            throw new IllegalArgumentException("ID has already been initialized");
        }

        mUID = id;
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

    /**
     * Override the equals method for User for easy testing
     * <p>
     * Based on: https://stackoverflow.com/questions/8180430/how-to-override-equals-method-in-java
     *
     * @param obj the object to compare to
     * @return boolean whether or not obj is the same User
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        User other = (User) obj;
        return (other.getUID() != null) && (other.getEmail() != null) &&
                (other.getUsername() != null) && other.getUID().equals(this.mUID) &&
                other.getEmail().equals(this.mEmail) && other.getUsername().equals(this.mUsername);
    }
}
