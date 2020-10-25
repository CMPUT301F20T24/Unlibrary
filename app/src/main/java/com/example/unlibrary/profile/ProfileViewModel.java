/*
 * ProfileViewModel
 *
 * October 25, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */
package com.example.unlibrary.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Manages updating user profile information
 */
public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mUserName = new MutableLiveData<>();
    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    private ProfileRepository mProfileRepository;

    /**
     * Initialize Profile repository and fetches current user information from repo
     */
    public ProfileViewModel() {
        mProfileRepository = new ProfileRepository();
    }

    /**
     * Gets mutable live data to facilitate two way binding username
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
     * @return email
     */
    public MutableLiveData<String> getEmail() {
        if (mEmail == null) {
            mEmail = new MutableLiveData<>();
        }
        return mEmail;
    }

    /**
     * Update user's profile in repository
     * TODO Be able to edit profile picture
     */
    public void updateProfile() {
        mProfileRepository.updateEmail(mEmail.getValue(), isUpdated -> {
            // TODO: update UI to show update success or error
            } );
        mProfileRepository.updateUserName(mUserName.getValue(), isUpdate -> {
            // TODO: update UI to show update success or err
        });
    }

    public void fetchUser() {
        mProfileRepository.fetchCurrentUser((s, userName, email) -> {
            if (s) {
                mUserName.setValue(userName);
                mEmail.setValue(email);
            }
        });
    }
}
