/*
 * CreateAccountCancelTest
 *
 * Nov 05, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


/**
 * Check if the Cancel button works on Create Account page
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateAccountCancelTest {

    private static final String TAG = "CreateAccountCancel";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void logout() {
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {
            Log.e(TAG, "Failed to logout.");
        }
    }

    @Test
    public void createAccountCancelTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.create_account_button), withText("Create Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        3),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        onView(allOf(withId(R.id.register_title))).check(matches(withText("Register")));
        onView(withId(R.id.register_username_input));
        onView(withId(R.id.register_email_input));
        onView(withId(R.id.login_password_input));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.cancel_button), withText("Cancel"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        4),
                                0),
                        isDisplayed()));
        materialButton2.perform(click());

        onView(allOf(withId(R.id.login_title))).check(matches(withText("Login")));

    }

    /**
     * Verifies the position of the child in a given view
     * @param parentMatcher
     * @param position
     * @return
     */
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
