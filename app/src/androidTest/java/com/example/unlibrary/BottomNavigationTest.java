package com.example.unlibrary;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.unlibrary.library.LibraryFragment;
import com.example.unlibrary.unlibrary.UnlibraryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class BottomNavigationTest extends MockLogin {

    private static Map<Integer, String> navBarMenuItems = Map.of(
            R.id.libraryFragment, "Library",
            R.id.unlibraryFragment, "Unlibrary",
            R.id.exchangeFragment, "Exchange",
            R.id.profileFragment, "Profile"
    );

    private static BottomNavigationView mBottomNavigationView;

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
                    if (!(navBarMenuItems.containsKey(id)) && !(navBarMenuItems.get(id).equals(title))) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Test
    public void testItems() {
        onView(withId(R.id.bottom_navigation)).check(matches(withUnlibraryMenuItems()));
    }

    @Test
    public void testNavigationSelectionListener() {
        MainActivity activity = activityScenarioRule.getScenario();
        BottomNavigationView.OnNavigationItemSelectedListener mockedListener =
                mock(BottomNavigationView.OnNavigationItemSelectedListener.class);
    }

}
