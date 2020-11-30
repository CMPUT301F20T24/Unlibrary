package com.example.unlibrary;


import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LibraryFlowTest extends MockLogin {
    private final String mTitle1 = "DO NOT INTERACT WITH THIS BOOK";
    private final String mTitle2 = "DO NOT INTERACT WITH THIS BOOK!";
    private final String mAuthor1 = "IT WILL BREAK THE TESTS";
    private final String mAuthor2 = "IT WILL BREAK THE TESTS!";
    private final String mIsbn1 = "1234567890123";
    private final String mIsbn2 = "1234567890129";
    private final int SLEEP_TIME = 800; // milliseconds

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void libraryFlowTest() throws InterruptedException {
        // Filter button is there
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        withParent(allOf(withId(R.id.frameLayout2),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        // Add book button is there
        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.fabAdd), withContentDescription("Add book"),
                        withParent(allOf(withId(R.id.frameLayout2),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));

        // Click on add book button
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fabAdd), withContentDescription("Add book"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Auto-fill button is there
        ViewInteraction button = onView(
                allOf(withId(R.id.auto_fill_button), withText("AUTO-FILL"), withContentDescription("Button to auto-fill fields"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        // Add photo button is there
        ViewInteraction imageButton3 = onView(
                allOf(withId(R.id.add_book_photo), withContentDescription("Add photo of book"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        imageButton3.check(matches(isDisplayed()));

        // Title input is there
        ViewInteraction editText = onView(
                allOf(withHint("Title"),
                        withParent(withParent(withId(R.id.book_title_input))),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

        // Author input is there
        ViewInteraction editText2 = onView(
                allOf(withHint("Author"),
                        withParent(withParent(withId(R.id.book_author_input))),
                        isDisplayed()));
        editText2.check(matches(isDisplayed()));

        // ISBN input is there
        ViewInteraction editText3 = onView(
                allOf(withHint("ISBN"),
                        withParent(withParent(withId(R.id.book_isbn_input))),
                        isDisplayed()));
        editText3.check(matches(isDisplayed()));

        // Save button is there
        ViewInteraction button2 = onView(
                allOf(withId(R.id.save_button), withText("SAVE"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        // Enter book title
        ViewInteraction textInputEditText6 = onView(withId(R.id.book_title_input_field));
        textInputEditText6.perform(replaceText(mTitle1), closeSoftKeyboard());

        // Enter book author
        ViewInteraction textInputEditText8 = onView(withId(R.id.book_author_input_field));
        textInputEditText8.perform(replaceText(mAuthor1), closeSoftKeyboard());

        // Enter book isbn
        ViewInteraction textInputEditText10 = onView(withId(R.id.book_isbn_input_field));
        textInputEditText10.perform(replaceText(mIsbn1), closeSoftKeyboard());

        // Save new book
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.save_button), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                7),
                        isDisplayed()));
        materialButton2.perform(click());

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Edit button is there
        ViewInteraction imageButton4 = onView(
                allOf(withId(R.id.edit_book), withContentDescription("Edit book"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        imageButton4.check(matches(isDisplayed()));

        // Delete button is there
        ViewInteraction imageButton5 = onView(
                allOf(withId(R.id.delete_book), withContentDescription("Delete book"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        imageButton5.check(matches(isDisplayed()));

        // Title label is there
        ViewInteraction textView = onView(withId(R.id.textView6));
        textView.check(matches(withText("TITLE")));

        // Title is correct
        ViewInteraction textView2 = onView(withId(R.id.textView7));
        textView2.check(matches(withText(mTitle1)));

        // Author label is there
        ViewInteraction textView3 = onView(withId(R.id.textView8));
        textView3.check(matches(withText("AUTHOR")));

        // Author is correct
        ViewInteraction textView4 = onView(withId(R.id.textView9));
        textView4.check(matches(withText(mAuthor1)));

        // Isbn label is there
        ViewInteraction textView5 = onView(withId(R.id.textView));
        textView5.check(matches(withText("ISBN")));

        // Isbn is correct
        ViewInteraction textView6 = onView(withId(R.id.textView5));
        textView6.check(matches(withText(mIsbn1)));

        // Status is available
        ViewInteraction view = onView(
                allOf(withId(R.id.status), withText("Available"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        // Navigate back to library
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.libraryFragment), withContentDescription("Library"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Verify title on card
        ViewInteraction textView8 = onView(
                allOf(withId(R.id.bookTitle),
                        withParent(withParent(allOf(childAtPosition(withId(R.id.book_list_container), 0), withId(R.id.item_card)))),
                        isDisplayed()));
        textView8.check(matches(withText(mTitle1)));

        // Verify author on card
        ViewInteraction textView9 = onView(
                allOf(withId(R.id.bookAuthor),
                        withParent(withParent(allOf(childAtPosition(withId(R.id.book_list_container), 0), withId(R.id.item_card)))),
                        isDisplayed()));
        textView9.check(matches(withText(mAuthor1)));

        // Verify book is availabe on card
        ViewInteraction view2 = onView(
                allOf(withId(R.id.status), withText("Available"),
                        withParent(withParent(allOf(childAtPosition(withId(R.id.book_list_container), 0), withId(R.id.item_card)))),
                        isDisplayed()));
        view2.check(matches(isDisplayed()));

        // Click on filter button
        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        // Filter by accepted
        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(0);
        appCompatCheckedTextView.perform(click());

        // Lock in filter
        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton3.perform(scrollTo(), click());

        // Verify there is still a card shown
        ViewInteraction viewGroup = onView(
                allOf(childAtPosition(withId(R.id.book_list_container), 0), withId(R.id.item_card)));
        viewGroup.check(matches(isDisplayed()));

        // Click filter button
        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        // Uncheck available
        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(0);
        appCompatCheckedTextView2.perform(click());

        // Check accepted
        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(2);
        appCompatCheckedTextView3.perform(click());

        // Lock in filter
        ViewInteraction materialButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton4.perform(scrollTo(), click());

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Verify list is empty
        ViewInteraction emptyList = onView(withId(R.id.book_list_container));
        emptyList.check(matches(isDisplayed()))
                .check(matches(not(hasDescendant(any(View.class)))));

        // Click filter button
        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton4.perform(click());

        // De-select accepeted option
        DataInteraction appCompatCheckedTextView4 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(2);
        appCompatCheckedTextView4.perform(click());

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
                allOf(withId(R.id.book_list_container),
                        childAtPosition(
                                withId(R.id.library_book_list),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        // Click on the edit book button
        ViewInteraction floatingActionButton5 = onView(
                allOf(withId(R.id.edit_book), withContentDescription("Edit book"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.library_book_frame),
                                        0),
                                2)));
        floatingActionButton5.perform(scrollTo(), click());

        // Auto-fill button is there
        ViewInteraction button3 = onView(
                allOf(withId(R.id.auto_fill_button), withContentDescription("Button to auto-fill fields"),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        // Add photo button is there
        ViewInteraction imageButton6 = onView(
                allOf(withId(R.id.add_book_photo), withContentDescription("Add photo of book"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        imageButton6.check(matches(isDisplayed()));

        // Title is correct
        ViewInteraction editText5 = onView(
                allOf(withText(mTitle1),
                        withParent(withParent(withId(R.id.book_title_input))),
                        isDisplayed()));
        editText5.check(matches(withText(mTitle1)));

        // Author is correct
        ViewInteraction editText7 = onView(
                allOf(withText(mAuthor1),
                        withParent(withParent(withId(R.id.book_author_input))),
                        isDisplayed()));
        editText7.check(matches(withText(mAuthor1)));

        // Isbn is correct
        ViewInteraction editText9 = onView(
                allOf(withText(mIsbn1),
                        withParent(withParent(withId(R.id.book_isbn_input))),
                        isDisplayed()));
        editText9.check(matches(withText(mIsbn1)));

        // Save button is there
        ViewInteraction button4 = onView(
                allOf(withId(R.id.save_button), withText("SAVE"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        button4.check(matches(isDisplayed()));

        ViewInteraction textInputEditText13 = onView(withId(R.id.book_title_input_field));
        textInputEditText13.perform(replaceText(mTitle2), closeSoftKeyboard());

        // Update author
        ViewInteraction textInputEditText15 = onView(withId(R.id.book_author_input_field));
        textInputEditText15.perform(replaceText(mAuthor2), closeSoftKeyboard());

        // Update isbn
        ViewInteraction textInputEditText17 = onView(withId(R.id.book_isbn_input_field));
        textInputEditText17.perform(replaceText(mIsbn2), closeSoftKeyboard());

        // Save updated book
        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.save_button), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                7),
                        isDisplayed()));
        materialButton6.perform(click());

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Navigate back to library
        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.libraryFragment), withContentDescription("Library"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Verify title on card
        ViewInteraction textView10 = onView(
                allOf(withId(R.id.bookTitle),
                        withParent(withParent(allOf(childAtPosition(withId(R.id.book_list_container), 0), withId(R.id.item_card)))),
                        isDisplayed()));
        textView10.check(matches(withText(mTitle2)));

        // Verify author on card
        ViewInteraction textView11 = onView(
                allOf(withId(R.id.bookAuthor),
                        withParent(withParent(allOf(childAtPosition(withId(R.id.book_list_container), 0), withId(R.id.item_card)))),
                        isDisplayed()));
        textView11.check(matches(withText(mAuthor2)));

        // Click on first card
        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.book_list_container),
                        childAtPosition(
                                withId(R.id.library_book_list),
                                0)));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));

        // Click on delete book button
        ViewInteraction floatingActionButton7 = onView(
                allOf(withId(R.id.delete_book), withContentDescription("Delete book"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.library_book_frame),
                                        0),
                                3)));
        floatingActionButton7.perform(scrollTo(), click());

        // Verify dialog title
        ViewInteraction textView12 = onView(
                allOf(withId(R.id.alertTitle), withText("Delete " + mTitle2 + "?"),
                        withParent(allOf(withId(R.id.title_template),
                                withParent(withId(R.id.topPanel)))),
                        isDisplayed()));
        textView12.check(matches(withText("Delete " + mTitle2 + "?")));

        // Verify dialog message
        ViewInteraction textView13 = onView(
                allOf(withId(android.R.id.message), withText("Do you really want to delete this book?"),
                        withParent(withParent(withId(R.id.scrollView))),
                        isDisplayed()));
        textView13.check(matches(withText("Do you really want to delete this book?")));

        // Verify dialog no button
        ViewInteraction button5 = onView(
                allOf(withId(android.R.id.button2), withText("NO"),
                        withParent(withParent(withId(R.id.buttonPanel))),
                        isDisplayed()));
        button5.check(matches(isDisplayed()));

        // Verify dialog yes button
        ViewInteraction button6 = onView(
                allOf(withId(android.R.id.button1), withText("YES"),
                        withParent(withParent(withId(R.id.buttonPanel))),
                        isDisplayed()));
        button6.check(matches(isDisplayed()));

        // Answer yes to delete
        ViewInteraction materialButton7 = onView(
                allOf(withId(android.R.id.button1), withText("Yes"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton7.perform(scrollTo(), click());

        // This is a hack because we don't use idling resources on our network calls
        Thread.sleep(SLEEP_TIME);

        // Verify list is empty
        ViewInteraction emptyList2 = onView(withId(R.id.book_list_container));
        emptyList2.check(matches(isDisplayed()))
                .check(matches(not(hasDescendant(any(View.class)))));
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
