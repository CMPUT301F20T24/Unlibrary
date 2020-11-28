package com.example.unlibrary;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class UnlibraryFLowTest{
    static final String mTitle = "UnlibraryTest1";
    static final String mIsbn = "1234567891111";
    static final String mAuthor = "uitests2";
    static final String mStatus = "Accepted";
    private final int SLEEP_TIME = 800; // milliseconds

    @Rule
    public final ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        onView(
                allOf(
                        withText("Unlibrary"),
                        isDescendantOfA(withId(R.id.bottom_navigation)),
                        isDisplayed()
                )
        ).perform(click());

    }

    @Test
    public void unlibraryflowtest() throws InterruptedException {

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);


        // Verify name on card
        ViewInteraction textView = onView(
                allOf(withId(R.id.bookTitle),
                        withParent(withParent(allOf(childAtPosition(withId(R.id.list), 0), withId(R.id.item_card)))),
                        isDisplayed()));
        textView.check(matches(withText(mTitle)));

        // Verify author on card
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.bookAuthor),
                        withParent(withParent(allOf(childAtPosition(withId(R.id.list), 0), withId(R.id.item_card)))),
                        isDisplayed()));
        textView2.check(matches(withText(mAuthor)));

        // Verify book is requested on card
        ViewInteraction view = onView(
                allOf(withId(R.id.status), withText(mStatus),
                        withParent(withParent(allOf(childAtPosition(withId(R.id.list), 0), withId(R.id.item_card)))),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        // Filter button is there
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        withParent(allOf(withId(R.id.frameLayout),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        // Click on filter button
        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        // Filter by requested
        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(0);
        appCompatCheckedTextView.perform(click());

        // Lock in filter
        ViewInteraction materialButton = onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton.perform(scrollTo(), click());

        // Verify list is empty
        ViewInteraction emptyList = onView(withId(R.id.list));
        emptyList.check(matches(isDisplayed()))
                .check(matches(not(hasDescendant(any(View.class)))));

        // Click filter button
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Uncheck available
        DataInteraction appCompatCheckedTextView1 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(0);
        appCompatCheckedTextView1.perform(click());

        // Check accepted
        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(1);
        appCompatCheckedTextView2.perform(click());

        // Lock in filter
        ViewInteraction materialButton2= onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Verify there is still a card shown
        ViewInteraction viewGroup = onView(
                allOf(childAtPosition(withId(R.id.list), 0), withId(R.id.item_card)));
        viewGroup.check(matches(isDisplayed()));

        // Click filter button
        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        // De-select accepeted option
        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(1);
        appCompatCheckedTextView3.perform(click());

        // Check borrowed
        DataInteraction appCompatCheckedTextView4 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(2);
        appCompatCheckedTextView4.perform(click());

        // Lock in filter
        ViewInteraction materialButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton4.perform(scrollTo(), click());

        Thread.sleep(SLEEP_TIME);

        // Verify list is empty
        emptyList = onView(withId(R.id.list));
        emptyList.check(matches(isDisplayed()))
                .check(matches(not(hasDescendant(any(View.class)))));

        // Click filter button
        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton4.perform(click());

        // de-checked borrowed
        DataInteraction appCompatCheckedTextView5 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(2);
        appCompatCheckedTextView5.perform(click());

        // Lock in filter
        ViewInteraction materialButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton5.perform(scrollTo(), click());

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Click on first book card in list
        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.unlibrary_book_list),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        Thread.sleep(SLEEP_TIME);

        // Verify title label
        ViewInteraction textView3 = onView(withId(R.id.textView6));
        textView3.check(matches(withText("Title")));
        // Verify title
        ViewInteraction textView4 = onView(withId(R.id.textView7));
        textView4.check(matches(withText(mTitle)));

        // Verify author label
        ViewInteraction textView5 = onView(withId(R.id.textView8));
        textView5.check(matches(withText("Author")));
        // Verify title
        ViewInteraction textView6 = onView(withId(R.id.textView9));
        textView6.check(matches(withText(mAuthor)));

        // Verify ISBN label
        ViewInteraction textView7 = onView(withId(R.id.textView));
        textView7.check(matches(withText("ISBN")));
        // Verify ISBN
        ViewInteraction textView8 = onView(withId(R.id.textView5));
        textView8.check(matches(withText(mIsbn)));

        // Verify request label
        ViewInteraction textView9 = onView(withId(R.id.textView2));
        textView9.check(matches(withText("Request Status")));
        // Verify request
        ViewInteraction textView10 = onView(withId(R.id.chip));
        textView10.check(matches(withText("ACCEPTED")));



        // Verify request label
        ViewInteraction textView11 = onView(withId(R.id.ownerUsername));
        textView11.check(matches(withText("UITests2")));
        // Verify request
        ViewInteraction textView12 = onView(withId(R.id.ownerEmail));
        textView12.check(matches(withText("uitests2@gmail.com")));

        // Verify request
        ViewInteraction textView13 = onView(withId(R.id. mapTitle));
        textView13.check(matches(withText("Handoff Location")));
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
