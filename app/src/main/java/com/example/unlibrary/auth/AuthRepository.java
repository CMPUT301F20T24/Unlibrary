/*
 * AuthRepository
 *
 * October 21, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.auth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

// TODO
public class AuthRepository {
    private FirebaseFirestore mDB;
    private FirebaseAuth mAuth;

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

    // TODO
    public void register(String email, String password, String username, OnCompleteListener<AuthResult> onComplete) {
        // Verify username is globally unique

        // Register user with Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(onComplete);

        // Create user document and link with auth
    }
}
