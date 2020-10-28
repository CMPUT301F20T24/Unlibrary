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

import com.example.unlibrary.util.SingleLiveEvent;

/**
 * Manages updating user profile information
 */
public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mUserName = new MutableLiveData<>("");
    private MutableLiveData<String> mEmail = new MutableLiveData<>("");
    private MutableLiveData<String> mPassword = new MutableLiveData<>("");
    private SingleLiveEvent<Pair<InputKey, String>> mInvalidInputEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> mUpdatedProfileEvent = new SingleLiveEvent<>();
    private ProfileRepository mProfileRepository = new ProfileRepository();
    private String mOldUserName;
    private String mOldEmail;

    /**
     * Constructs Profile ViewModel
     */
    public ProfileViewModel() {
    }

    /**
     * Gets mutable live data to facilitate two way binding username
     *
     * @return username
     */
    public MutableLiveData<String> getUserName() {
        if (mUserName == null) {
            mUserName = new MutableLiveData<>();
        }
        return mUserName;
    }

    /**
     * Gets mutable live data to facilitate two way binding email
     *
     * @return email
     */
    public MutableLiveData<String> getEmail() {
        if (mEmail == null) {
            mEmail = new MutableLiveData<>();
        }
        return mEmail;
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
     * UpdatedProfileEvent getter for activity observers
     *
     * @return Event of successful profile update
     */
    public SingleLiveEvent<Void> getUpdatedProfileEvent() {
        if (mUpdatedProfileEvent == null) {
            mUpdatedProfileEvent = new SingleLiveEvent<>();
        }
        return mUpdatedProfileEvent;
    }

    /**
     * Save current state of profile information
     */
    public void saveTextFields() {
        mOldEmail = mEmail.getValue();
        mOldUserName = mUserName.getValue();
    }

    /**
     * Reset text fields to previous state
     */
    public void resetTextFields() {
        mEmail.setValue(mOldEmail);
        mUserName.setValue(mOldUserName);
    }

    public void clearPassword() {
        mPassword.setValue("");
    }

    /**
     * Fetches the current user and sets the username and email
     */
    public void fetchUser() {
        mProfileRepository.fetchCurrentUser((s, userName, email) -> {
            if (s) {
                mUserName.setValue(userName);
                mEmail.setValue(email);
            }
        });
    }

    /**
     * Attempts to update user profile with new username and email
     * <p>
     * Needs to re-authenticate the user then update the database and firebase auth
     */
    public void attemptUpdateProfile() {
        if (mPassword.getValue().equals("")) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, "Enter Password"));
            return;
        }
        mProfileRepository.reAuthenticateUser(mOldEmail, mPassword.getValue(), isLoggedIn -> {
            if (isLoggedIn) {
                mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, null));
                mProfileRepository.updateEmail(mEmail.getValue(), isEmailUpdated -> {
                    if (isEmailUpdated) {
                        mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, null));
                        if (mOldUserName != mUserName.getValue()) {
                            mProfileRepository.updateUserName(mUserName.getValue(), isUserNameUpdated -> {
                                if (isUserNameUpdated) {
                                    mInvalidInputEvent.setValue(new Pair<>(InputKey.USERNAME, null));
                                    mUpdatedProfileEvent.call();
                                } else {
                                    mInvalidInputEvent.setValue(new Pair<>(InputKey.USERNAME, "Username taken"));
                                }
                            });
                        } else {
                            mUpdatedProfileEvent.call();
                        }

                    } else {
                        mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, "Invalid Email"));
                    }
                });
            } else {
                mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, "Incorrect Password"));
            }
        });
    }


    public enum InputKey {
        EMAIL,
        PASSWORD,
        USERNAME
    }

    /**
     * Callback interface for simple completion
     */
    public interface OnFinishedListener {
        void finished(Boolean succeeded);
    }
}
