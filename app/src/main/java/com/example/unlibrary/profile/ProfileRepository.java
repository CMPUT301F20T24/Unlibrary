/*
 * ProfileRepository
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.profile;

import com.example.unlibrary.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import javax.inject.Inject;

/**
 * Manages all the database interaction for the Profile ViewModel
 */
public class ProfileRepository {

    private final static String USERS_COLLECTION = "users";
    private final static String UID_FIELD = "id";
    private final static String USERNAME_FIELD = "username";
    private final static String EMAIL_FIELD = "email";

    private FirebaseFirestore mDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUID;

    /**
     * Get instances of firestore database and firebase auth
     */
    @Inject
    public ProfileRepository(FirebaseFirestore db, FirebaseAuth auth) {
        mDB = db;
        mAuth = auth;
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
                .document(mUID)
                .get()
                .addOnSuccessListener(task -> {
                    onFinished.finished(true, task.toObject(User.class));
                });
    }

    /**
     * Updating user's email requires re-authentication.
     *
     * @param email              users original email
     * @param password           users password
     * @param onFinishedListener callback to notify viewmodel
     */
    public void reAuthenticateUser(String email, String password, OnFinishedListener onFinishedListener) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        mUser.reauthenticate(credential).addOnCompleteListener(task -> {
            onFinishedListener.finished(task.isSuccessful());
        });
    }

    /**
     * Updating user's profile in firestore and firebase auth
     *
     * @param user                    updated user information
     * @param onEmailErrorListener    callback for errors regarding updating the email
     * @param onUsernameErrorListener callback for errors regarding updating the username
     * @param onFinished              callback used when updates are finished
     */
    public void updateUserProfile(User user, OnErrorListener onEmailErrorListener, OnErrorListener onUsernameErrorListener, OnFinishedListener onFinished) {
        mAuth.getCurrentUser().updateEmail(user.getEmail()).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                CollectionReference usersRef = mDB.collection(USERS_COLLECTION);
                Query query = usersRef.whereEqualTo(USERNAME_FIELD, user.getUsername());
                query.get().addOnCompleteListener(task -> {
                    // Checks if it's a unique username OR if the username is unchanged
                    if (task.getResult().isEmpty() || task.getResult().getDocuments().get(0).getId().equals(mUID)) {
                        mDB.collection(USERS_COLLECTION)
                                .document(mUID)
                                .set(user)
                                .addOnCompleteListener(dbTask -> {
                                    onFinished.finished(dbTask.isSuccessful());
                                });
                    } else {
                        onUsernameErrorListener.error();
                    }

                });
            } else {
                onEmailErrorListener.error();
            }
        });
    }

    /**
     * Callback interface for asynchronous nature of fetching user
     */
    public interface OnFinishedFetchListener {
        void finished(Boolean succeeded, User user);
    }

    /**
     * Simple callback interface for asynchronous events
     */
    public interface OnFinishedListener {
        void finished(Boolean succeeded);
    }

    /**
     * Callback interface for errors with asynchronous events
     */
    public interface OnErrorListener {
        void error();
    }
}
