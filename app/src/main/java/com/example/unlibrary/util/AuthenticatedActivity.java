package com.example.unlibrary.util;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.unlibrary.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class AuthenticatedActivity extends AppCompatActivity {

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            // Not logged in
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
        System.out.println(mUser == null);
    }

    // TODO improve this, it's not great because theoretically the user could be logged out and we would get a null exception error
    protected FirebaseUser getUser() {
        if (mUser == null) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        }
        return mUser;
    }
}
