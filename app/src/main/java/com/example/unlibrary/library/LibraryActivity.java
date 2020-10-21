package com.example.unlibrary.library;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.example.unlibrary.R;
import com.example.unlibrary.util.AuthenticatedActivity;

public class LibraryActivity extends AuthenticatedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
    }
}
