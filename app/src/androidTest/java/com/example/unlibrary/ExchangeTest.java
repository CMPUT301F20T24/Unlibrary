package com.example.unlibrary;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public class ExchangeTest extends MockLogin {
    private static MainActivity mActivity;
    private final int SLEEP_TIME = 5000; // milliseconds

    @ClassRule
    public static ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setupActivity() {
        // Get activity from scenario rule
        rule.getScenario().onActivity(activity -> mActivity = activity);
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

        // Click on first match
        ViewInteraction bookMatch = onView(
                allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.exchange_book_list),
                                0)));
        bookMatch.perform(actionOnItemAtPosition(0, click()));

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Ensure book is there
        ViewInteraction bookTitle = onView(allOf(withId(R.id.textView6), withText("Hit Refresh")));
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

        // Click on first book
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.list),
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

        // Ensure request successfully sent message is sent
        // https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso
        ViewInteraction successToast = onView(withText("Request successfully sent"))
                .inRoot(withDecorView(not(mActivity.getWindow().getDecorView())));
        successToast.check(matches(isDisplayed()));
    }
}
