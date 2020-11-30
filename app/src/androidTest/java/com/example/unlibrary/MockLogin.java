/*
 * MockLogin
 *
 * November 29, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.unlibrary.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Abstract class to deal with getting passed Auth
 */
@RunWith(AndroidJUnit4.class)
public abstract class MockLogin {
    protected static final String mEmail = "uitests@gmail.com";
    protected static final String mPassword = "password";
    private static final int SLEEP = 1000;

    @Rule
    public ActivityScenarioRule<AuthActivity> mAcitivityScenarioRule = new ActivityScenarioRule<>(AuthActivity.class);

    @BeforeClass
    public static void setup() throws InterruptedException {
        FirebaseAuth.getInstance().signOut();
        Thread.sleep(SLEEP);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail, mPassword);
        Thread.sleep(SLEEP);
    }

    @AfterClass
    public static void cleanup() {
        FirebaseAuth.getInstance().signOut();
    }
}
