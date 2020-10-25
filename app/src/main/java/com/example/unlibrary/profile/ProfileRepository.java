/*
 * ProfileRepository
 *
 * October 22, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Manages all the database interaction for the Profile ViewModel
 */
public class ProfileRepository {

    private final String USERS_COLLECTION = "users";
    private final String UID_FIELD = "id";
    private final String EMAIL_FIELD = "email";
    private final String USERNAME_FIELD = "username";

    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mDB;
    private FirebaseAuth mAuth;
    private String mUID;

    /**
     * Constructor for the Profile Repository
     */
    public ProfileRepository() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUID = mFirebaseUser.getUid();
    }

    /**
     * Gets the current user email and username from firestore
     * @param onFinished callback to notify completed query
     */
    public void fetchCurrentUser(OnFinishedFetchListener onFinished) {
        mDB.collection(USERS_COLLECTION)
                .whereEqualTo(UID_FIELD, mUID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = (String) document.getData().get(EMAIL_FIELD);
                            String username = (String) document.getData().get(USERNAME_FIELD);
                            onFinished.finished(true, username, email);
                        }
                    } else {
                        // TODO will raise some sort of UI error but this should never happen
                    }
                });
    }

    /**
     * Updates the users email in firebase auth and firestore
     * @param email new email to update
     * @param onFinished callback to notify completed task
     */
    public void updateEmail(String email, OnFinishedUpdateFieldListener onFinished) {
        mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                mDB.collection(USERS_COLLECTION)
                        .document(mUID)
                        .update(EMAIL_FIELD, email)
                        .addOnCompleteListener(dbTask -> onFinished.finished(dbTask.isSuccessful()));
            }
        });
    }

    /**
     * Updates the user's username in firestore
     * @param username new username to update
     * @param onFinished callback to notify completed task
     */
    public void updateUserName(String username, OnFinishedUpdateFieldListener onFinished) {
        mDB.collection(USERS_COLLECTION)
                .document(mUID)
                .update(USERNAME_FIELD, username)
                .addOnCompleteListener(dbTask -> onFinished.finished(dbTask.isSuccessful()));
    }

    /**
     * Callback interface for asynchronous nature of fetching user
     */
    public interface OnFinishedFetchListener {
        void finished(Boolean succeeded, String userName, String email);
    }

    /**
     * Callback interface for asynchronous nature of updating user's info
     */
    public interface OnFinishedUpdateFieldListener {
        void finished(Boolean succeeded);
    }
}
