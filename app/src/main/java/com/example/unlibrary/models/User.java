package com.example.unlibrary.models;

/**
 * Represents a user in our application domain. This user information is stored in Firestore and is
 * related to a user entry in Firebase.
 *
 * TODO: Link with Firebase
 * TODO: Add authorization to secure allowed methods (e.g. editing user data)
 */
public class User {
    String mId;
    String mName;
    String mUsername;
    String mEmail;
    String mPhone;

    /**
     * Constructs a user.
     *
     * @param id unique ID associated with the user, obtained from Firebase Auth
     * @param name actual/display name of the user
     * @param username that uniquely represents the user other than its ID
     * @param email of the user
     * @param phone number of the user
     */
    public User(String id, String name, String username, String email, String phone) {
        mId = id;
        mName = name;
        mUsername = username;
        mEmail = email;
        mPhone = phone;
    }

    /**
     * Returns the unique identifier of the user.
     *
     * @return unique user id associated with the user
     */
    public String getId() {
        return mId;
    }

    /**
     * Returns the user's actual/display name.
     *
     * @return actual/display name
     */
    public String getName() {
        return mName;
    }

    /**
     * Updates the user name. Can only be done to the current user.
     *
     * @param name that replaces the old user name
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the user's username.
     *
     * @return username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Updates the current username. Can only be done to the current user.
     *
     * @param username that replaces the old username
     */
    public void setUsername(String username) {
        mUsername = username;
    }

    /**
     * Returns the user's email.
     *
     * @return email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Updates the current user's email. Can only be done to the current user.
     *
     * @param email that replaces the old email
     */
    public void setEmail(String email) {
        mEmail = email;
    }

    /**
     * Returns the user's phone number
     *
     * @return phone number
     */
    public String getPhone() {
        return mPhone;
    }

    /**
     * Updates the current user's phone number. Can only be done to the current user.
     *
     * @param phone that replaces the old phone number
     */
    public void setPhone(String phone) {
        mPhone = phone;
    }
}
