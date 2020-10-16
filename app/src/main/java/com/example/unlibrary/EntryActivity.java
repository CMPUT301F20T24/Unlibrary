package com.example.unlibrary;

import android.content.Intent;
import android.os.Bundle;

import com.example.unlibrary.profile.ProfileActivity;
import com.example.unlibrary.util.AuthenticatedActivity;

public class EntryActivity extends AuthenticatedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        // TODO add logic to route user to whatever activity they were last in. Store this info in sharedPreferences maybe

        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
