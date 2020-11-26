package com.example.unlibrary;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@RunWith(AndroidJUnit4.class)
public abstract class MockLogin {
    @Rule
    public final ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private final String mEmail = "cdiego@ualberta.ca";
    private final String mPassword = "password123";
    private boolean mIsSignedIn = false;

    @Before
    public void login() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail, mPassword)
                .addOnSuccessListener(authResult -> mIsSignedIn = true);

        await().atMost(5, TimeUnit.SECONDS).until(() -> mIsSignedIn);
    }

    @After
    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
