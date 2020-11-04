/*
 * AuthenticatedActivity
 *
 * October 18, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.util;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unlibrary.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This abstract activity locks any activities that extend it behind a layer of Firebase authentication.
 * That is, if the phone does not have an active user session it will reroute the user to the auth
 * activity immediately. This design means that any possible entry points into (launcher, notifications, etc.)
 * will be protected by the authentication.
 */
public abstract class AuthenticatedActivity extends AppCompatActivity {

    private FirebaseUser mUser;

    /**
     * Checks if user is authenticated and if not redirects the app to the AuthActivity.
     *
     * @param savedInstanceState Saved bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            goToAuth();
        }
    }

    /**
     * Logs current user out of the app and goes back to the Auth activity
     */
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        goToAuth();
    }

    /**
     * Starts Auth Activity
     */
    private void goToAuth() {
        // Not logged in
        Intent intent = new Intent(this, AuthActivity.class);
        // TODO we should send an extra with the intent which is the original activity the user was trying to navigate to, this way we can route them there after signing in
        startActivity(intent);
        finish();
    }

    /**
     * Returns the current FirebaseUser, if it's null (edge case) will go back to the
     * Auth Activity
     *
     * @return current FirebaseUser
     */
    protected FirebaseUser getUser() {
        if (mUser == null) {
            goToAuth();
        }
        return mUser;
    }
}
