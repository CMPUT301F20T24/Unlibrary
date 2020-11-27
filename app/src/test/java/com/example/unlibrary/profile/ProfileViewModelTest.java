/*
 * ProfileViewModelTest
 *
 * November 26, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.profile;

import android.os.Build;
import android.util.Pair;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.unlibrary.models.User;
import com.example.unlibrary.util.AuthUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.unlibrary.helper.LiveDataTestUtil.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for ProfileViewModel to ensure the MutableLiveData's are updating properly
 * and the viewmodel is saving state
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class ProfileViewModelTest {
    private static final String mId = "1234";
    private static final String mUserName = "testUsername1";
    private static final String mEmail = "testEmail@gmail.com";
    private static final String mPassword = "test-password";
    private static final User mUpdatedUser = new User(mId, "testUpdatedUsername1", "test-updated-emaile@gmail.com");
    private static final Pair<AuthUtil.InputKey, String> mPasswordInputReset = new Pair<>(AuthUtil.InputKey.PASSWORD, null);
    private static final Pair<AuthUtil.InputKey, String> mEmailInputReset = new Pair<>(AuthUtil.InputKey.EMAIL, null);
    private static final Pair<AuthUtil.InputKey, String> mUsernameInputReset = new Pair<>(AuthUtil.InputKey.USERNAME, null);
    ProfileViewModel mViewModel;
    User mUser;

    @Mock
    ProfileRepository mMockRepository;

    @Mock
    Observer<Pair<AuthUtil.InputKey, String>> mMockObserver;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        mUser = new User(mId, mUserName, mEmail);

        mMockRepository = mock(ProfileRepository.class);
        doAnswer(invocation -> {
            ProfileRepository.OnFinishedFetchListener successCallback = invocation.getArgument(0);
            successCallback.finished(true, mUser);
            return null;
        }).when(mMockRepository).fetchCurrentUser(any());

        doAnswer(invocation -> {
            ProfileRepository.OnFinishedListener successCallback = invocation.getArgument(2);
            successCallback.finished(true);
            return null;
        }).when(mMockRepository).reAuthenticateUser(eq(mEmail), eq(mPassword), any());

        doAnswer(invocation -> {
            ProfileRepository.OnFinishedListener successCallback = invocation.getArgument(2);
            successCallback.finished(true);
            return null;
        }).when(mMockRepository).updateUserProfile(eq(mUpdatedUser), any(), any(), any());

        mViewModel = new ProfileViewModel(mMockRepository);

        mMockObserver = (Observer<Pair<AuthUtil.InputKey, String>>) mock(Observer.class);
        mViewModel.getInvalidInputEvent().observeForever(mMockObserver);
    }

    @Test
    public void testGetUser() {
        assertEquals(mUser, getOrAwaitValue(mViewModel.getUser()));
        mViewModel.getUser().setValue(mUpdatedUser);
        assertNotEquals(mUser, getOrAwaitValue(mViewModel.getUser()));
    }

    @Test
    public void getPassword() {
        mViewModel.getPassword().setValue(mPassword);
        assertEquals(mPassword, getOrAwaitValue(mViewModel.getPassword()));
    }

    @Test
    public void testAttemptUpdateProfile() {
        mViewModel.getPassword().setValue(mPassword);
        assertEquals(mPassword, getOrAwaitValue(mViewModel.getPassword()));
        mViewModel.getUser().setValue(mUpdatedUser);
        assertEquals(mUpdatedUser, getOrAwaitValue(mViewModel.getUser()));

        mViewModel.attemptUpdateProfile();

        ArrayList<Pair<AuthUtil.InputKey, String>> resetInputEvents = new ArrayList<>(Arrays.asList(mPasswordInputReset, mEmailInputReset, mUsernameInputReset));
        ArgumentCaptor<Pair<AuthUtil.InputKey, String>> argument = ArgumentCaptor.forClass(Pair.class);
        verify(mMockObserver, times(3)).onChanged(argument.capture());
        assertEquals(resetInputEvents, argument.getAllValues());
        assertEquals(mUpdatedUser, getOrAwaitValue(mViewModel.getUser()));
    }

    @Test
    public void testSaveAndResetUserInfo() {
        mViewModel.saveUserInfo();
        mViewModel.getUser().setValue(mUpdatedUser);
        mViewModel.resetUserInfo();
        assertEquals(mUser, getOrAwaitValue(mViewModel.getUser()));
        assertEquals("", getOrAwaitValue(mViewModel.getPassword()));

        ArrayList<Pair<AuthUtil.InputKey, String>> resetInputEvents = new ArrayList<>(Arrays.asList(mPasswordInputReset, mEmailInputReset, mUsernameInputReset));
        ArgumentCaptor<Pair<AuthUtil.InputKey, String>> argument = ArgumentCaptor.forClass(Pair.class);
        verify(mMockObserver, times(3)).onChanged(argument.capture());
        assertEquals(resetInputEvents, argument.getAllValues());
    }
}
