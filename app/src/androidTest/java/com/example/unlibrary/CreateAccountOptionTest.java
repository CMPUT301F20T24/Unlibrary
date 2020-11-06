package com.example.unlibrary;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateAccountOptionTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void createAccountOptionTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.create_account_button), withText("Create Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        3),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textInputEditText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.register_username_input),
                                0),
                        1),
                        isDisplayed()));
        textInputEditText.perform(replaceText("test2"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.register_email_input),
                                0),
                        1),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("test2@gmail.com"), closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.register_password_input),
                                0),
                        1),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("hi1234"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.register_button), withText("Register"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        4),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());
    }

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
