/*
 * ProfileViewModelTest
 *
 * November 26, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.profile;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.unlibrary.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.example.unlibrary.helper.LiveDataTestUtil.getOrAwaitValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for ProfileViewModel to ensure the MutableLiveData's are updating properly
 * and the viewmodel is saving state
 */
@RunWith(PowerMockRunner.class)
public class ProfileViewModelTest {
    private static final String mId = "1234";
    private static final String mUserName = "test-username";
    private static final String mEmail = "test-email@gmail.com";
    private static final String mPassword = "test-password";
    private static final User mUpdatedUser = new User(mId, "test-updated-username", "test-updated-emaile@gmail.com");

    ProfileViewModel mViewModel;
    User mUser;

    @Mock
    ProfileRepository mMockRepository;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private void setupMocks() {
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
    }

    @Before
    public void setup() {
        mUser = new User(mId, mUserName, mEmail);
        mMockRepository = mock(ProfileRepository.class);
        setupMocks();

        mViewModel = new ProfileViewModel(mMockRepository);
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
        mViewModel.getUser().setValue(mUpdatedUser);
        mViewModel.attemptUpdateProfile();
        assertEquals(mUpdatedUser, getOrAwaitValue(mViewModel.getUser()));
    }

    @Test
    public void testSaveAndResetUserInfo() {
        mViewModel.saveUserInfo();
        mViewModel.getUser().setValue(mUpdatedUser);
        mViewModel.resetUserInfo();
        assertEquals(mUser, getOrAwaitValue(mViewModel.getUser()));
        assertEquals("", getOrAwaitValue(mViewModel.getPassword()));

    }
}
