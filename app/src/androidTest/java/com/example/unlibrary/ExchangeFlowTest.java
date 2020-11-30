/*
 * ExchangeFlowTest
 *
 * November 29, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.unlibrary.models.Book;
import com.example.unlibrary.models.Request;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * UI test for exchange page flow
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ExchangeFlowTest extends MockLogin {
    private final int SLEEP_TIME = 5000; // milliseconds

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

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

    @Test
    public void searchBookTest() throws InterruptedException {
        // Navigate to exchange view
        ViewInteraction exchangeBottomNav = onView(
                allOf(withId(R.id.exchangeFragment), withContentDescription("Exchange"),
                        isDescendantOfA(withId(R.id.bottom_navigation)),
                        isDisplayed()));
        exchangeBottomNav.perform(click());

        // Search for hit refresh book
        ViewInteraction searchBar = onView(withId(R.id.search_exchange_book));
        searchBar.perform(replaceText("hit refresh"), pressImeActionButton(), closeSoftKeyboard());
        Thread.sleep(SLEEP_TIME);

        // Click on first match
        ViewInteraction bookMatch = onView(
                allOf(withId(R.id.book_list_container),
                        childAtPosition(
                                withId(R.id.exchange_book_list),
                                0)));
        bookMatch.perform(actionOnItemAtPosition(0, click()));

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Ensure book is there
        ViewInteraction bookTitle = onView(allOf(withId(R.id.textView7), withText("Hit Refresh")));
        bookTitle.check(matches(isDisplayed()));
    }

    @Test
    public void requestBookTest() throws InterruptedException {
        // Go to exchange
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.exchangeFragment), withContentDescription("Exchange"),
                        isDescendantOfA(withId(R.id.bottom_navigation)),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction searchBar = onView(withId(R.id.search_exchange_book));
        searchBar.perform(replaceText("exchangeuitest"), pressImeActionButton(), closeSoftKeyboard());
        Thread.sleep(SLEEP_TIME);

        // Click on first book
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.book_list_container),
                        childAtPosition(
                                withId(R.id.exchange_book_list),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Click make request
        ViewInteraction requestButton = onView(
                allOf(withId(R.id.add_request), withContentDescription(R.string.add_request_description),
                        isDescendantOfA(withId(R.id.exchange_book_frame)),
                        isDisplayed()));
        requestButton.perform(click());
    }

    @After
    public void resetRequest() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .document("fD92L0B71VzVmJrSX7UR")
                .update("status", Book.Status.AVAILABLE.toString());


        db.collection("requests")
                .whereEqualTo("book", "fD92L0B71VzVmJrSX7UR")
                .whereNotEqualTo("state", Request.State.ARCHIVED)
                .get()
                .addOnSuccessListener(task -> {
                    db.runTransaction(transaction -> {
                        List<DocumentSnapshot> docs = task.getDocuments();
                        if (docs.size() == 0) {
                            return null;
                        }

                        final DocumentReference requestDocument = db.collection("requests").document(docs.get(0).getId());
                        transaction.update(requestDocument, "state", Request.State.ARCHIVED.toString());

                        return null;
                    });
                });

    }
}
