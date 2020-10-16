package com.example.unlibrary.unlibrary;

import android.os.Bundle;

import com.example.unlibrary.R;
import com.example.unlibrary.util.AuthenticatedActivity;

public class UnlibraryActivity extends AuthenticatedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlibrary);
    }
}
