/*
 * EntryActivity
 *
 * October 18, 2020
 *
 * TODO copyright information
 */

package com.example.unlibrary;

import android.content.Intent;
import android.os.Bundle;

import com.example.unlibrary.profile.ProfileActivity;
import com.example.unlibrary.util.AuthenticatedActivity;

/**
 * This is the the main gateway into the app, primarily through the launcher. The only work that
 * should be done here is to direct the user to other activities that can do the actual work.
 */
public class EntryActivity extends AuthenticatedActivity {

    /**
     * Navigate to a screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        // TODO add logic to route user to whatever activity they were last in. Store this info in sharedPreferences maybe

        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
