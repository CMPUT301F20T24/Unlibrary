package com.example.unlibrary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unlibrary.library.LibraryActivity;
import com.example.unlibrary.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Context switch between logged in or logged out
        boolean loggedIn = true;
        Intent intent = new Intent(this,
                loggedIn ? LibraryActivity.class : LoginActivity.class);
        startActivity(intent);
    }
}
