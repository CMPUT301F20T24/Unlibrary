/*
 * ProfileRepository
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.profile;


import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;


/**
 * Manages all the database interaction for the Profile ViewModel
 */
public class ProfileRepository {

    private final static String USERS_COLLECTION = "users";
    private final static String UID_FIELD = "id";
    private final static String EMAIL_FIELD = "email";
    private final static String USERNAME_FIELD = "username";

    private FirebaseFirestore mDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUID;

    /**
     * Get instances of firestore database and firebase auth
     */
    public ProfileRepository() {
        mDB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Gets the current user email and username from firestore
     *
     * @param onFinished callback to notify completed query
     */
    public void fetchCurrentUser(OnFinishedFetchListener onFinished) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUID = mUser.getUid();
        mDB.collection(USERS_COLLECTION)
                .whereEqualTo(UID_FIELD, mUID)
                .get()
                .addOnSuccessListener(task -> {
                    List<DocumentSnapshot> document = task.getDocuments();
                    DocumentSnapshot userInfo = document.get(0);
                    String email = (String) userInfo.get(EMAIL_FIELD);
                    String username = (String) userInfo.get(USERNAME_FIELD);
                    onFinished.finished(true, username, email);
                });
    }

    /**
     * Updates the users email in firebase auth and firestore
     *
     * @param email      new email to update
     * @param onFinished callback to notify completed task
     */
    public void updateEmail(String email, OnFinishedListener onFinished) {
        mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                mDB.collection(USERS_COLLECTION)
                        .document(mUID)
                        .update(EMAIL_FIELD, email)
                        .addOnCompleteListener(dbTask -> onFinished.finished(dbTask.isSuccessful()));
            } else {
                onFinished.finished(false);
            }
        });
    }

    /**
     * First check if updated username is globally unique (i.e. not taken)
     * if so, updates the user's username in firestore
     *
     * @param username   new username to update
     * @param onFinished callback to notify completed task
     */
    public void updateUserName(String username, OnFinishedListener onFinished) {
        // Verify username is globally unique
        CollectionReference usersRef = mDB.collection(USERS_COLLECTION);
        Query query = usersRef.whereEqualTo(USERNAME_FIELD, username);
        query.get().addOnCompleteListener(task -> {
            if (task.getResult().isEmpty()) {
                mDB.collection(USERS_COLLECTION)
                        .document(mUID)
                        .update(USERNAME_FIELD, username)
                        .addOnCompleteListener(dbTask -> onFinished.finished(dbTask.isSuccessful()));
            } else {
                onFinished.finished(false);
            }
        });
    }

    /**
     * Updating user's email requires re-authentication.
     *
     * @param email      users email
     * @param password   users password
     * @param onFinished callback function to facilitate updating profile
     */
    public void reAuthenticateUser(String email, String password, OnFinishedListener onFinished) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mUser.reauthenticate(credential).addOnCompleteListener(task -> {
            onFinished.finished(task.isSuccessful());
        });
    }

    /**
     * Callback interface for asynchronous nature of fetching user
     */
    public interface OnFinishedFetchListener {
        void finished(Boolean succeeded, String userName, String email);
    }

    /**
     * Callback interface for simple completion
     */
    public interface OnFinishedListener {
        void finished(Boolean succeeded);
    }
}
