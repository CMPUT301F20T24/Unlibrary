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
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ManualAddBookTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void manualAddBookTest() {
        ViewInteraction textInputEditText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.login_email_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("golnoush@gmail.cm"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.login_password_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("hihihihi"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.login_button), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        3),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withText("golnoush@gmail.cm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_email_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("golnoush@gmail.com"));

        ViewInteraction textInputEditText4 = onView(
                allOf(withText("golnoush@gmail.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.login_email_input),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText4.perform(closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.login_button), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        3),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());

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

        ViewInteraction textInputEditText5 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.book_title_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText5.perform(replaceText("test"), closeSoftKeyboard());

        ViewInteraction textInputEditText6 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.book_author_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText6.perform(replaceText("someone"), closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.book_isbn_input),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText7.perform(replaceText("9780441016078"), closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.save_button), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                6),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.libraryFragment), withContentDescription("Library"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
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
