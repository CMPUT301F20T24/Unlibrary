package com.example.unlibrary;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public abstract class MockLogin {
    private final String mEmail = "cdiego@ualberta.ca";
    private final String mPassword = "password123";

    @Rule
    public final ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void login() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail, mPassword);
    }
}
