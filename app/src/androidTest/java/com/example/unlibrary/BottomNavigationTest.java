package com.example.unlibrary;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


@RunWith(AndroidJUnit4.class)
public class BottomNavigationTest extends MockLogin {

    private static Map<Integer, String> mNavBarItems = Map.of(
            R.id.libraryFragment, "Library",
            R.id.unlibraryFragment, "Unlibrary",
            R.id.exchangeFragment, "Exchange",
            R.id.profileFragment, "Profile"
    );

    // https://www.vogella.com/tutorials/AndroidTestingEspresso/article.html
    public static Matcher<View> withUnlibraryMenuItems() {
        return new BoundedMatcher<View, BottomNavigationView>(BottomNavigationView.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(BottomNavigationView bottomNavigationView) {
                Menu menu = bottomNavigationView.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem current = menu.getItem(i);
                    int id = current.getItemId();
                    String title = (String) current.getTitle();
                    if (!(mNavBarItems.containsKey(id)) && !(mNavBarItems.get(id).equals(title))) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static Matcher<View> isChecked(int id) {
        return new BoundedMatcher<View, BottomNavigationView>(BottomNavigationView.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(BottomNavigationView bottomNavigationView) {
                return bottomNavigationView.getMenu().findItem(id).isChecked();
            }
        };
    }

    @Test
    public void menuItemsTest() {
        onView(withId(R.id.bottom_navigation)).check(matches(withUnlibraryMenuItems()));
    }

    @Test
    public void selectedMenuItemTest() {
        for (Map.Entry<Integer, String> entry : mNavBarItems.entrySet()) {
            onView(
                    allOf(
                            withText(entry.getValue()),
                            isDescendantOfA(withId(R.id.bottom_navigation)),
                            isDisplayed()
                    )
            ).perform(click());
            onView(withId(R.id.bottom_navigation)).check(matches(isChecked(entry.getKey())));
        }
    }
}
