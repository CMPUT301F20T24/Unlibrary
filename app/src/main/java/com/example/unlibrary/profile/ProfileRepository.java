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

    public void fetchCurrentUser(OnFinishedFetchListener onFinished) {
        mDB.collection(USERS_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String UID = (String) document.getData().get(UID_FIELD);
                            if (UID != null && UID.equals(mUID)) {
                                String email = (String) document.getData().get(EMAIL_FIELD);
                                String username = (String) document.getData().get(USERNAME_FIELD);
                                onFinished.finished(true, username, email);
                            }
                        }
                    }
                });
    }

    public void updateEmail(String email, OnFinishedUpdateEmailListener onFinished) {
        mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                mDB.collection(USERS_COLLECTION)
                        .document(mUID)
                        .update(EMAIL_FIELD, email)
                        .addOnCompleteListener(dbTask -> onFinished.finished(dbTask.isSuccessful()));
            }
        });
    }

    public interface OnFinishedFetchListener {
        void finished(Boolean succeeded, String userName, String email);
    }

    public interface OnFinishedUpdateEmailListener {
        void finished(Boolean succeeded);
    }
}
