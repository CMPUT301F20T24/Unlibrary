package com.example.unlibrary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unlibrary.auth.AuthActivity;
import com.example.unlibrary.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        Intent intent;
        if (user == null) {
            intent = new Intent(this, AuthActivity.class);
        } else {
            // TODO remember what activity the user was last in
            intent = new Intent(this, ProfileActivity.class);
        }
        startActivity(intent);
    }
}
