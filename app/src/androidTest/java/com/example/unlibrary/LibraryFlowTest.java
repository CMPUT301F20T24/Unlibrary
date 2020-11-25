package com.example.unlibrary;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LibraryFlowTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void libraryFlowTest() {
        ViewInteraction textInputEditText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.login_email_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("uitests@gm"), closeSoftKeyboard());

        pressBack();

        ViewInteraction textInputEditText2 = onView(
                allOf(withText("uitests@gm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_email_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("uitests@gmail.com"));

        ViewInteraction textInputEditText3 = onView(
                allOf(withText("uitests@gmail.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_email_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(closeSoftKeyboard());

        pressBack();

        ViewInteraction textInputEditText4 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.login_password_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("password"), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(withText("password"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_password_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText5.perform(pressImeActionButton());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.login_button), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        3),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Add book"),
                        withParent(allOf(withId(R.id.frameLayout2),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.fabAdd), withContentDescription("Filter"),
                        withParent(allOf(withId(R.id.frameLayout2),
                                withParent(withId(R.id.nav_host_fragment)))),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fabAdd), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.auto_fill_button), withText("AUTO-FILL"), withContentDescription("Button to auto-fill fields"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction imageButton3 = onView(
                allOf(withId(R.id.add_book_photo), withContentDescription("Add photo of book"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        imageButton3.check(matches(isDisplayed()));

        ViewInteraction editText = onView(
                allOf(withText("Title"),
                        withParent(withParent(withId(R.id.book_title_input))),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction editText2 = onView(
                allOf(withText("Author"),
                        withParent(withParent(withId(R.id.book_author_input))),
                        isDisplayed()));
        editText2.check(matches(isDisplayed()));

        ViewInteraction editText3 = onView(
                allOf(withText("ISBN"),
                        withParent(withParent(withId(R.id.book_isbn_input))),
                        isDisplayed()));
        editText3.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.save_button), withText("SAVE"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction textInputEditText6 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.book_title_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText6.perform(replaceText("a"), closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
                allOf(withText("a"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_title_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText7.perform(pressImeActionButton());

        ViewInteraction textInputEditText8 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.book_author_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText8.perform(replaceText("b"), closeSoftKeyboard());

        ViewInteraction textInputEditText9 = onView(
                allOf(withText("b"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_author_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText9.perform(pressImeActionButton());

        ViewInteraction textInputEditText10 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.book_isbn_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText10.perform(replaceText("1234567890123"), closeSoftKeyboard());

        ViewInteraction textInputEditText11 = onView(
                allOf(withText("1234567890123"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_isbn_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText11.perform(pressImeActionButton());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.save_button), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                7),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction imageButton4 = onView(
                allOf(withId(R.id.edit_book), withContentDescription("Edit book"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        imageButton4.check(matches(isDisplayed()));

        ViewInteraction imageButton5 = onView(
                allOf(withId(R.id.delete_book), withContentDescription("Delete book"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        imageButton5.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.textView6), withText("TITLE"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        textView.check(matches(withText("TITLE")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textView7), withText("a"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        textView2.check(matches(withText("a")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.textView8), withText("AUTHOR"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        textView3.check(matches(withText("AUTHOR")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.textView9), withText("b"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        textView4.check(matches(withText("b")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.textView), withText("ISBN"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        textView5.check(matches(withText("ISBN")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.textView5), withText("1234567890123"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        textView6.check(matches(withText("1234567890123")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.textView2), withText("REQUEST STATUS"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        textView7.check(matches(withText("REQUEST STATUS")));

        ViewInteraction view = onView(
                allOf(withId(R.id.status), withText("Available"),
                        withParent(withParent(withId(R.id.library_book_frame))),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.libraryFragment), withContentDescription("Library"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.bookTitle), withText("a"),
                        withParent(withParent(withId(R.id.item_card))),
                        isDisplayed()));
        textView8.check(matches(withText("a")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.bookAuthor), withText("b"),
                        withParent(withParent(withId(R.id.item_card))),
                        isDisplayed()));
        textView9.check(matches(withText("b")));

        ViewInteraction view2 = onView(
                allOf(withId(R.id.status), withText("Available"),
                        withParent(withParent(withId(R.id.item_card))),
                        isDisplayed()));
        view2.check(matches(isDisplayed()));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.library_book_list),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.libraryFragment), withContentDescription("Library"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Add book"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(0);
        appCompatCheckedTextView.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton3.perform(scrollTo(), click());

        ViewInteraction viewGroup = onView(
                allOf(withParent(allOf(withId(R.id.item_card),
                        withParent(withId(R.id.list)))),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Add book"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(0);
        appCompatCheckedTextView2.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(1);
        appCompatCheckedTextView3.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton4.perform(scrollTo(), click());

        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.fabFilter), withContentDescription("Add book"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                1),
                        isDisplayed()));
        floatingActionButton4.perform(click());

        DataInteraction appCompatCheckedTextView4 = onData(anything())
                .inAdapterView(allOf(withId(R.id.select_dialog_listview),
                        childAtPosition(
                                withId(R.id.contentPanel),
                                0)))
                .atPosition(1);
        appCompatCheckedTextView4.perform(click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton5.perform(scrollTo(), click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.library_book_list),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction floatingActionButton5 = onView(
                allOf(withId(R.id.edit_book), withContentDescription("Edit book"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.library_book_frame),
                                        0),
                                2)));
        floatingActionButton5.perform(scrollTo(), click());

        pressBack();

        ViewInteraction button3 = onView(
                allOf(withId(R.id.auto_fill_button), withText("AUTO-FILL"), withContentDescription("Button to auto-fill fields"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        ViewInteraction imageButton6 = onView(
                allOf(withId(R.id.add_book_photo), withContentDescription("Add photo of book"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        imageButton6.check(matches(isDisplayed()));

        ViewInteraction editText4 = onView(
                allOf(withText("a"),
                        withParent(withParent(withId(R.id.book_title_input))),
                        isDisplayed()));
        editText4.check(matches(isDisplayed()));

        ViewInteraction editText5 = onView(
                allOf(withText("a"),
                        withParent(withParent(withId(R.id.book_title_input))),
                        isDisplayed()));
        editText5.check(matches(withText("a")));

        ViewInteraction editText6 = onView(
                allOf(withText("b"),
                        withParent(withParent(withId(R.id.book_author_input))),
                        isDisplayed()));
        editText6.check(matches(isDisplayed()));

        ViewInteraction editText7 = onView(
                allOf(withText("b"),
                        withParent(withParent(withId(R.id.book_author_input))),
                        isDisplayed()));
        editText7.check(matches(withText("b")));

        ViewInteraction editText8 = onView(
                allOf(withText("1234567890123"),
                        withParent(withParent(withId(R.id.book_isbn_input))),
                        isDisplayed()));
        editText8.check(matches(isDisplayed()));

        ViewInteraction editText9 = onView(
                allOf(withText("1234567890123"),
                        withParent(withParent(withId(R.id.book_isbn_input))),
                        isDisplayed()));
        editText9.check(matches(withText("1234567890123")));

        ViewInteraction button4 = onView(
                allOf(withId(R.id.save_button), withText("SAVE"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        button4.check(matches(isDisplayed()));

        ViewInteraction textInputEditText12 = onView(
                allOf(withText("a"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_title_input),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText12.perform(click());

        ViewInteraction textInputEditText13 = onView(
                allOf(withText("a"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_title_input),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText13.perform(replaceText("ab"));

        ViewInteraction textInputEditText14 = onView(
                allOf(withText("ab"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_title_input),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText14.perform(closeSoftKeyboard());

        pressBack();

        ViewInteraction textInputEditText15 = onView(
                allOf(withText("b"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_author_input),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText15.perform(replaceText("bc"));

        ViewInteraction textInputEditText16 = onView(
                allOf(withText("bc"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_author_input),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText16.perform(closeSoftKeyboard());

        pressBack();

        ViewInteraction textInputEditText17 = onView(
                allOf(withText("1234567890123"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_isbn_input),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText17.perform(replaceText("1234567890129"));

        ViewInteraction textInputEditText18 = onView(
                allOf(withText("1234567890129"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_isbn_input),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText18.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText19 = onView(
                allOf(withText("1234567890129"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.book_isbn_input),
                                        0),
                                1),
                        isDisplayed()));
        textInputEditText19.perform(pressImeActionButton());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.save_button), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                7),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.libraryFragment), withContentDescription("Library"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.bookTitle), withText("ab"),
                        withParent(withParent(withId(R.id.item_card))),
                        isDisplayed()));
        textView10.check(matches(withText("ab")));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.bookAuthor), withText("bc"),
                        withParent(withParent(withId(R.id.item_card))),
                        isDisplayed()));
        textView11.check(matches(withText("bc")));

        ViewInteraction floatingActionButton6 = onView(
                allOf(withId(R.id.fabAdd), withContentDescription("Filter"),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout2),
                                        childAtPosition(
                                                withId(R.id.nav_host_fragment),
                                                0)),
                                2),
                        isDisplayed()));
        floatingActionButton6.perform(click());

        pressBack();

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.list),
                        childAtPosition(
                                withId(R.id.library_book_list),
                                0)));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction floatingActionButton7 = onView(
                allOf(withId(R.id.delete_book), withContentDescription("Delete book"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.library_book_frame),
                                        0),
                                3)));
        floatingActionButton7.perform(scrollTo(), click());

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.alertTitle), withText("Delete ab?"),
                        withParent(allOf(withId(R.id.title_template),
                                withParent(withId(R.id.topPanel)))),
                        isDisplayed()));
        textView12.check(matches(withText("Delete ab?")));

        ViewInteraction textView13 = onView(
                allOf(withId(android.R.id.message), withText("Do you really want to delete this book?"),
                        withParent(withParent(withId(R.id.scrollView))),
                        isDisplayed()));
        textView13.check(matches(withText("Do you really want to delete this book?")));

        ViewInteraction button5 = onView(
                allOf(withId(android.R.id.button2), withText("NO"),
                        withParent(withParent(withId(R.id.buttonPanel))),
                        isDisplayed()));
        button5.check(matches(isDisplayed()));

        ViewInteraction button6 = onView(
                allOf(withId(android.R.id.button1), withText("YES"),
                        withParent(withParent(withId(R.id.buttonPanel))),
                        isDisplayed()));
        button6.check(matches(isDisplayed()));

        ViewInteraction materialButton7 = onView(
                allOf(withId(android.R.id.button1), withText("Yes"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton7.perform(scrollTo(), click());
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
