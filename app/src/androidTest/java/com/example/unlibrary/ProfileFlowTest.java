/*
 * ProfileFlowTest
 *
 * November 29, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * UI test for profile page flow
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ProfileFlowTest extends MockLogin {
    private final int SLEEP_TIME = 2000; // milliseconds
    private static final String mUsername = "UiTests";
    private final String mUpdatedUsername = "UiTests123";

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void before() throws InterruptedException {
        onView(
                allOf(
                        withText("Profile"),
                        isDescendantOfA(withId(R.id.bottom_navigation)),
                        isDisplayed()
                )
        ).perform(click());
        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);
    }

    @Test
    public void profileViewTest() {
        // Verify username label
        onView(
                allOf(
                        withId(R.id.editable_text_title),
                        withParent((withId(R.id.username))),
                        isDisplayed(),
                        withText("Username")));
        // Verify username
        onView(
                allOf(
                        withId(R.id.editable_text_content),
                        withParent((withId(R.id.username))),
                        isDisplayed(),
                        withText(mUsername)));


        // Verify email label
        onView(
                allOf(
                        withId(R.id.editable_text_title),
                        withParent((withId(R.id.email))),
                        isDisplayed(),
                        withText("Email")));
        // Verify email
        onView(
                allOf(
                        withId(R.id.editable_text_content),
                        withParent((withId(R.id.email))),
                        isDisplayed(),
                        withText(mEmail)));
    }

    @Test
    public void editUsernameTest() throws InterruptedException {
        // Verify edit button
        ViewInteraction editButton = onView(
                allOf(withId(R.id.edit_button),
                        withParent((withId(R.id.profile_fragment))),
                        isDisplayed()));
        // Click edit button
        editButton.perform(click());

        // Verify username label
        onView(
                allOf(
                        withId(R.id.editable_text_title),
                        withParent((withId(R.id.username))),
                        isDisplayed(),
                        withText("Username")));
        // Verify username is editable
        ViewInteraction usernameEditField = onView(
                allOf(
                        withId(R.id.edit_text_input),
                        withParent(withParent(withParent(withId(R.id.username)))),
                        isDisplayed()));


        // Verify email label
        onView(
                allOf(
                        withId(R.id.editable_text_title),
                        withParent((withId(R.id.email))),
                        isDisplayed(),
                        withText("Email")));
        // Verify email is editable
        onView(
                allOf(
                        withId(R.id.edit_text_input),
                        withParent(withParent(withParent(withId(R.id.email)))),
                        isDisplayed(),
                        withText(mEmail)));

        // Verify password edit field is visible
        ViewInteraction passwordEditField = onView(
                allOf(
                        withId(R.id.password_input),
                        withParent(withParent(withId(R.id.password))),
                        isDisplayed()));

        // Verify confirm button is visible
        ViewInteraction confirmButton = onView(
                allOf(withId(R.id.confirm_button),
                        withParent((withId(R.id.profile_fragment))),
                        isDisplayed()));

        // Update user info
        usernameEditField.perform(replaceText(mUpdatedUsername), closeSoftKeyboard());
        passwordEditField.perform(replaceText(mPassword), closeSoftKeyboard());
        confirmButton.perform(click());
        Thread.sleep(SLEEP_TIME);

        // Verify username label
        onView(
                allOf(
                        withId(R.id.editable_text_title),
                        withParent((withId(R.id.username))),
                        isDisplayed(),
                        withText("Username")));
        // Verify username is updated
        onView(
                allOf(
                        withId(R.id.editable_text_content),
                        withParent((withId(R.id.username))),
                        isDisplayed(),
                        withText(mUpdatedUsername)));


        // Verify email label
        onView(
                allOf(
                        withId(R.id.editable_text_title),
                        withParent((withId(R.id.email))),
                        isDisplayed(),
                        withText("Email")));
        // Verify email is not updated
        onView(
                allOf(
                        withId(R.id.editable_text_content),
                        withParent((withId(R.id.email))),
                        isDisplayed(),
                        withText(mEmail)));
    }

    @AfterClass
    public static void resetUsername() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document("FTWgM8tUDXUuDnQQZX1CbIJ3B8x2").update("username", mUsername);
    }
}
