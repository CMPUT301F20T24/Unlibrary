package com.example.unlibrary;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
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
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ExchangeTest extends MockLogin {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

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
    public void searchBookTest() {
        // Navigate to exchange view
        ViewInteraction exchangeBottomNav = onView(
                allOf(withId(R.id.exchangeFragment), withContentDescription("Exchange"),
                        isDescendantOfA(withId(R.id.bottom_navigation)),
                        isDisplayed()));
        exchangeBottomNav.perform(click());

        // Search for hit refresh book
        ViewInteraction searchBar = onView(withId(R.id.search_exchange_book));
        searchBar.perform(replaceText("hit refresh"), pressImeActionButton(), closeSoftKeyboard());

        // Click on first match
        ViewInteraction bookMatch = onView(
                allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.exchange_book_list),
                                0)));
        bookMatch.perform(actionOnItemAtPosition(0, click()));

        // TODO: Verify title
    }

    @Test
    public void requestBookTest() {
        // Go to exchange
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.exchangeFragment), withContentDescription("Exchange"),
                        isDescendantOfA(withId(R.id.bottom_navigation)),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Click on first book
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.exchange_book_list),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        // Click make request
        ViewInteraction requestButton = onView(
                allOf(withId(R.id.add_request), withContentDescription("Edit book"),
                        isDescendantOfA(withId(R.id.exchange_book_frame)),
                        isDisplayed()));
        requestButton.perform(click());

        // TODO: Verify request made
    }
}
