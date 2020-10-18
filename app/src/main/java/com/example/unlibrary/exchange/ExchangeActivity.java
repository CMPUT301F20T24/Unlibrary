package com.example.unlibrary.exchange;

import android.os.Bundle;

import com.example.unlibrary.R;
import com.example.unlibrary.util.AuthenticatedActivity;

public class ExchangeActivity extends AuthenticatedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
    }
}
