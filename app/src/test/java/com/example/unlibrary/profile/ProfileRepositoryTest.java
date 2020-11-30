/*
 * ProfileRepositoryTest
 *
 * November 27, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.profile;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.example.unlibrary.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.os.Looper.getMainLooper;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

/**
 * Profile Repository unit testing using firebase emulator
 */
@RunWith(RobolectricTestRunner.class)
@LooperMode(PAUSED)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class ProfileRepositoryTest {
    private final static String USERS_COLLECTION = "users";
    private final static String UID_FIELD = "id";
    private final static String USERNAME_FIELD = "username";
    private final static String EMAIL_FIELD = "email";

    private final Context mContext = ApplicationProvider.getApplicationContext();
    private ProfileRepository mRepository;

    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;

    private final int SLEEP_TIME = 10;
    private final int SLEEP_TIME_MILLIS = SLEEP_TIME * 1000;

    private static final String mUID = "123";
    private static final String mUsername = "username123";
    private static final String mEmail = "email123@gmail.com";
    private final User mUser = new User(mUID, mUsername, mEmail);

    @Before
    public void setup() throws InterruptedException {
        FirebaseApp.initializeApp(mContext);

        mDb = FirebaseFirestore.getInstance();
        mDb.useEmulator("127.0.0.1", 8080);

        AtomicBoolean setDocument = new AtomicBoolean(false);
        mDb.collection(USERS_COLLECTION).document(mUID).set(mUser).addOnCompleteListener(task -> {
            setDocument.set(task.isSuccessful());
        });

        // Callback to update repository books might not have been called yet
        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();
        await().atMost(SLEEP_TIME, SECONDS).until(setDocument::get);

        mAuth = mock(FirebaseAuth.class);
        FirebaseUser mockUser = mock(FirebaseUser.class);

        when(mockUser.getUid()).thenReturn(mUID);
        when(mAuth.getCurrentUser()).thenReturn(mockUser);

        mRepository = new ProfileRepository(mDb, mAuth);
    }

    @After
    public void cleanup() {
        mDb.terminate();
    }

    @Test
    public void testFetchCurrentUser() throws InterruptedException {
        AtomicBoolean fetchedUser = new AtomicBoolean(false);
        mRepository.fetchCurrentUser((bool, user) -> {
            fetchedUser.set(bool);
            assertTrue(bool);
            assertEquals(mUser, user);
        });

        // Callback to update repository books might not have been called yet
        Thread.sleep(SLEEP_TIME_MILLIS);
        shadowOf(getMainLooper()).idle();
        await().atMost(SLEEP_TIME, SECONDS).until(fetchedUser::get);
    }

    // This test is currently not functioning as Firebase Auth cannot be properly mocked
//    @Test
//    public void testUpdateUserProfile() throws InterruptedException {
//        // Update the user using profile repository
//        User newUser = new User(mUID, "newUsername123", "newEmail123@gmail.com");
//        AtomicBoolean updatedUser = new AtomicBoolean(false);
//        mRepository.updateUserProfile(newUser, Assert::fail, Assert::fail, Assert::assertTrue);
//
//        // Callback to update repository books might not have been called yet
//        Thread.sleep(SLEEP_TIME_MILLIS);
//        shadowOf(getMainLooper()).idle();
//        await().atMost(SLEEP_TIME, SECONDS).until(updatedUser::get);
//
//        // Check the database
//        AtomicBoolean fetchedUser = new AtomicBoolean(false);
//        mDb.collection(USERS_COLLECTION).document(mUID).get().addOnCompleteListener(task ->{
//            fetchedUser.set(true);
//            assertTrue(task.isSuccessful());
//            assertEquals(task.getResult().toObject(User.class), newUser);
//        });
//
//        // Callback to update repository books might not have been called yet
//        Thread.sleep(SLEEP_TIME_MILLIS);
//        shadowOf(getMainLooper()).idle();
//        await().atMost(SLEEP_TIME, SECONDS).until(fetchedUser::get);
//    }
}
