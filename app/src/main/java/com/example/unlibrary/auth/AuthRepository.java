/*
 * AuthRepository
 *
 * October 21, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.auth;

import com.example.unlibrary.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

// TODO
public class AuthRepository {
    private static final String USER_COLLECTION = "users";
    private static final String USERNAME_FIELD = "username";
    private final FirebaseFirestore mDB;
    private final FirebaseAuth mAuth;

    // TODO
    public AuthRepository() {
        this.mDB = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    // TODO
    public void signIn(String email, String password, OnCompleteListener<AuthResult> onComplete) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(onComplete);
    }

    /**
     * Register a new user in Firebase auth. Create a new user document with a globally unique username.
     * @param email User email
     * @param password User password
     * @param username User username
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
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                // Create user document and link with auth
                                String id = mAuth.getCurrentUser().getUid();
                                User newUser = new User(id, username, email);
                                usersRef.document(id).set(newUser).addOnCompleteListener(task3 -> {
                                    if (task3.isSuccessful()) {
                                        onFinished.finished(true, "");
                                    } else {
                                        onFinished.finished(false, "Failed to make user document. " + task3.getException().getMessage());
                                    }
                                });
                            } else {
                                onFinished.finished(false, "Failed to register user. " + task2.getException().getMessage());
                            }
                        });
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
