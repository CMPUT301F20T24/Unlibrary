/*
 * ExchangeFragment
 *
 * Nov 04, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
//package com.example.unlibrary;
//
//
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import androidx.test.espresso.ViewInteraction;
//import androidx.test.filters.LargeTest;
//import androidx.test.rule.ActivityTestRule;
//import androidx.test.runner.AndroidJUnit4;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//import org.hamcrest.core.IsInstanceOf;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.action.ViewActions.replaceText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withParent;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.allOf;
//
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class LoginActivityTest1 {
//
//    @Rule
//    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
//
//    @Test
//    public void loginActivityTest1() {
//        ViewInteraction textInputEditText = onView(
//                allOf(childAtPosition(
//                        childAtPosition(
//                                withId(R.id.login_email_input),
//                                0),
//                        0),
//                        isDisplayed()));
//        textInputEditText.perform(replaceText("golnoush@gmail.com"), closeSoftKeyboard());
//
//        ViewInteraction textInputEditText2 = onView(
//                allOf(childAtPosition(
//                        childAtPosition(
//                                withId(R.id.login_password_input),
//                                0),
//                        1),
//                        isDisplayed()));
//        textInputEditText2.perform(replaceText("hihihihi"), closeSoftKeyboard());
//
//        ViewInteraction materialButton = onView(
//                allOf(withId(R.id.login_button), withText("Login"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.linearLayout),
//                                        3),
//                                1),
//                        isDisplayed()));
//        materialButton.perform(click());
//    }
//
//    private static Matcher<View> childAtPosition(
//            final Matcher<View> parentMatcher, final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                        && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
//}
