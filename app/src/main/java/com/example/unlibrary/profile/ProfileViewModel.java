package com.example.unlibrary.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> mUserName = new MutableLiveData<>();
    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    private ProfileRepository mProfileRepository;

    public ProfileViewModel() {
        mProfileRepository = new ProfileRepository();
        mProfileRepository.fetchCurrentUser((s, userName, email) -> {
            if (s) {
                mUserName.setValue(userName);
                mEmail.setValue(email);
            }
        });
    }

    public MutableLiveData<String> getUserName() {
        if (mUserName == null) {
            mUserName = new MutableLiveData<>();
        }
        return mUserName;
    }

    public MutableLiveData<String> getEmail() {
        if (mEmail == null) {
            mEmail = new MutableLiveData<>();
        }
        return mEmail;
    }

    public void updateProfile() {
        mProfileRepository.updateEmail(mEmail.getValue(), isUpdated -> {
                // do toast notification and error on text field !!
            } );
    }
}
