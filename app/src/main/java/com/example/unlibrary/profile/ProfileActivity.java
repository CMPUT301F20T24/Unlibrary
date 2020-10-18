package com.example.unlibrary.profile;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.unlibrary.EntryActivity;
import com.example.unlibrary.R;
import com.example.unlibrary.util.AuthenticatedActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AuthenticatedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    // TODO this is a completely temporary method and UI just to demonstrate the logout capability
    public void logout(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        Intent intent = new Intent(this, EntryActivity.class);
        startActivity(intent);
    }
}
