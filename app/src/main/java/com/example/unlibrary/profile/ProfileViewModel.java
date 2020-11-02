/*
 * ProfileViewModel
 *
 * October 25, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.profile;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.models.User;
import com.example.unlibrary.util.AuthUtil;
import com.example.unlibrary.util.SingleLiveEvent;

import static com.example.unlibrary.util.AuthUtil.validateEmail;
import static com.example.unlibrary.util.AuthUtil.validateUsername;

/**
 * Manages updating user profile information
 */
public class ProfileViewModel extends ViewModel {

    private MutableLiveData<User> mUser = new MutableLiveData<>();
    private MutableLiveData<String> mPassword = new MutableLiveData<>("");
    private SingleLiveEvent<Pair<InputKey, String>> mInvalidInputEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mProfileUpdateEvent = new SingleLiveEvent<>();
    private ProfileRepository mProfileRepository = new ProfileRepository();

    private String mOldUserName;
    private String mOldEmail;

    /**
     * Constructs Profile ViewModel
     */
    public ProfileViewModel() {
        fetchUser();
    }

    /**
     * Gets Mutable live data to facilitate two
     *
     * @return user
     */
    public MutableLiveData<User> getUser() {
        if (mUser == null) {
            mUser = new MutableLiveData<>();
        }
        return mUser;
    }

    /**
     * Gets mutable live data to facilitate two way binding password
     *
     * @return password
     */
    public MutableLiveData<String> getPassword() {
        if (mPassword == null) {
            mPassword = new MutableLiveData<>();
        }
        return mPassword;
    }

    /**
     * InvalidInputEvent getter for activity observers.
     *
     * @return Event of failure message to display
     */
    public SingleLiveEvent<Pair<InputKey, String>> getInvalidInputEvent() {
        if (mInvalidInputEvent == null) {
            mInvalidInputEvent = new SingleLiveEvent<>();
        }
        return mInvalidInputEvent;
    }

    /**
     * ProfileUpdateEvent getter for activity observers
     *
     * @return Event for successful profile update
     */
    public SingleLiveEvent<Boolean> getProfileUpdateEvent() {
        if (mProfileUpdateEvent == null) {
            mProfileUpdateEvent = new SingleLiveEvent<>();
        }
        return mProfileUpdateEvent;
    }


    /**
     * Save current state of profile information
     */
    public void saveUserInfo() {
        mOldEmail = mUser.getValue().getEmail();
        mOldUserName = mUser.getValue().getUsername();
    }

    /**
     * Reset text fields to previous state
     */
    public void resetUserInfo() {
        mUser.setValue(new User(mUser.getValue().getId(), mOldUserName, mOldEmail));
        mPassword.setValue("");
        mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, null));
        mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, null));
        mInvalidInputEvent.setValue(new Pair<>(InputKey.USERNAME, null));
    }

    /**
     * Fetches the current user and sets the username and email
     */
    public void fetchUser() {
        mProfileRepository.fetchCurrentUser((s, user) -> {
            if (s) {
                mUser.setValue(user);
            }
        });
    }

    /**
     * Attempts to update user profile with new username and email
     * <p>
     * Needs to re-authenticate the user then update the database and firebase auth
     */
    public void attemptUpdateProfile() {
        // Clear all TextView's of error messages
        mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, null));
        mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, null));
        mInvalidInputEvent.setValue(new Pair<>(InputKey.USERNAME, null));

        // First validate updated info
        String email = "", username = "";
        boolean invalid = false;
        try {
            email = validateEmail(mUser.getValue().getEmail());
        } catch (AuthUtil.InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, e.getMessage()));
            invalid = true;
        }

        try {
            username = validateUsername(mUser.getValue().getUsername());
        } catch (AuthUtil.InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.USERNAME, e.getMessage()));
            invalid = true;
        }

        if (mPassword.getValue() == null || mPassword.getValue().equals("")) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, "Enter Password"));
            invalid = true;
        }

        if (invalid) {
            mProfileUpdateEvent.setValue(false);
            return;
        }

        mProfileRepository.reAuthenticateUser(mOldEmail, mPassword.getValue(), isLoggedIn -> {
            mPassword.setValue("");
            if (isLoggedIn) {
                mProfileRepository.updateUserProfile(mUser.getValue(),
                        () -> mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, "Invalid Email")),
                        () -> mInvalidInputEvent.setValue(new Pair<>(InputKey.USERNAME, "Username taken")),
                        (isProfileUpdated) -> mProfileUpdateEvent.setValue(isProfileUpdated)
                );

            } else {
                mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, "Incorrect Password"));
                mProfileUpdateEvent.setValue(false);
            }
        });
    }

    public enum InputKey {
        EMAIL,
        PASSWORD,
        USERNAME
    }
}
