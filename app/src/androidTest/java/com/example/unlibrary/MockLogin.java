package com.example.unlibrary;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.unlibrary.auth.AuthActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static androidx.test.espresso.Espresso.onIdle;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public abstract class MockLogin {
    protected static final String mEmail = "uitests@gmail.com";
    protected static final String mPassword = "password";
    private static final int SLEEP = 1000;

    @Rule
    public ActivityScenarioRule<AuthActivity> mAcitivityScenarioRule = new ActivityScenarioRule<>(AuthActivity.class);

    @BeforeClass
    public static void setup() throws InterruptedException {
        FirebaseAuth.getInstance().signOut();
        Thread.sleep(SLEEP);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail, mPassword);
        Thread.sleep(SLEEP);
    }

    @AfterClass
    public static void cleanup() {
        FirebaseAuth.getInstance().signOut();
    }
//    private final String mEmail = "uitests@gmail.com";
//    private final String mPassword = "password";
//    private static final String IDLING_NAME = "com.example.unlibrary.FireBaseTest.key.IDLING_NAME";
//    private static final CountingIdlingResource idlingResource = new CountingIdlingResource(IDLING_NAME);
//
//    @Rule
//    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
//
//    /**
//     * Ensures that app is on MainActivity authenticated as the correct test user.
//     */
//    @Before
//    public void prepare() throws InterruptedException {
//        FirebaseAuth.getInstance().signOut();
//        Thread.sleep(1000);
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail,mPassword);
//        Thread.sleep(1000);
//        Intent intent = new Intent(mActivityTestRule.getActivity().getApplicationContext(), MainActivity.class);
//        mActivityTestRule.getActivity().startActivity(intent);
//        // https://github.com/cutiko/espressofirebase Relevant resource
//        // TODO extract
////        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
////        int apps = FirebaseApp.getApps(context).size();
////        if (apps == 0) {
////            fail("App not initialized");
////        }
////        IdlingRegistry.getInstance().register(idlingResource);
////        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
////            // User is logged in. Must log in as correct user
////            FirebaseAuth.getInstance()
////                    .signInWithEmailAndPassword(mEmail, mPassword)
////                    .addOnCompleteListener(task -> {
////                        if (task.isSuccessful()) {
////                            idlingResource.decrement();
////                            Intent intent = new Intent(context, MainActivity.class);
////                            mActivityTestRule.getActivity().startActivity(intent);
////                        } else {
////                            fail("The user was not logged successfully");
////                        }
////                    });
////            idlingResource.increment();
////            onIdle();
////        } else {
////            AtomicReference<Boolean> once = new AtomicReference<>(false);
////            // User is logged in. Must log out and log back in as proper user
////            FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
////                // Logout finished. Log back in
////                if (once.get()) {
////                    return;
////                }
////                once.set(true);
////                firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
////                        .addOnCompleteListener(task -> {
////                            if (task.isSuccessful()) {
////                                idlingResource.decrement();
////                                Intent intent = new Intent(context, MainActivity.class);
////                                mActivityTestRule.getActivity().startActivity(intent);
////                            } else {
////                                fail("The user was not logged in successfully");
////                            }
////                        });
////            });
////            idlingResource.increment();
////            FirebaseAuth.getInstance().signOut();
////            onIdle();
////        }
//    }
//
//    /**
//     * Logs out when test is done.
//     */
//    @After
//    public void cleanup() {
//        FirebaseAuth.getInstance().signOut();
//    }
}
