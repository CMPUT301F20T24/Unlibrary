/*
 * AuthRepository
 *
 * October 21, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.auth;

import androidx.annotation.NonNull;

import com.example.unlibrary.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Manages interaction with Firebase for all auth functionality.
 */
public class AuthRepository {
    private static final String USER_COLLECTION = "users";
    private static final String USERNAME_FIELD = "username";
    private final FirebaseFirestore mDB;
    private final FirebaseAuth mAuth;

    /**
     * Construct a new AuthRepository
     */
    public AuthRepository() {
        this.mDB = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Sign in a user.
     *
     * @param email      User email
     * @param password   User password
     * @param onFinished Code to call when finished successfully or not
     */
    public void signIn(String email, String password, OnFinishedListener onFinished) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onFinished.finished(true, "");
                    } else {
                        onFinished.finished(false, task.getException().getMessage());
                    }
                });
    }

    /**
     * Register a new user in Firebase auth. Create a new user document with a globally unique username.
     *
     * @param email      User email
     * @param password   User password
     * @param username   User username
     * @param onFinished Code to call when finished successfully or not
     */
    public void register(String email, String password, String username, OnFinishedListener onFinished) {
        // Verify username is globally unique
        CollectionReference usersRef = mDB.collection(USER_COLLECTION);
        Query query = usersRef.whereEqualTo(USERNAME_FIELD, username);
        query.get().addOnCompleteListener(task -> {
            if (task.getResult().isEmpty()) {
                // Register user with Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            // Create user document and link with auth
                            String id = mAuth.getCurrentUser().getUid();
                            User newUser = new User(id, username, email);
                            usersRef.document(id).set(newUser)
                                    .addOnSuccessListener(aVoid -> onFinished.finished(true, ""))
                                    .addOnFailureListener(e -> onFinished.finished(false, "Failed to make user document. " + e.getMessage()));
                        })
                        .addOnFailureListener(e -> onFinished.finished(false, "Failed to register user. " + e.getMessage()));
            } else {
                onFinished.finished(false, "Username is not globally unique.");
            }
        });
    }

    /**
     * Callback interface for the asynchronous nature of register and signIn.
     */
    public interface OnFinishedListener {
        void finished(Boolean succeeded, String msg);
    }
}
