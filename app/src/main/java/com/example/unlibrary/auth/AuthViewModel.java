package com.example.unlibrary.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {

    private MutableLiveData<String> mEmail;
    private MutableLiveData<String> mPassword;

    public MutableLiveData<String> getEmail() {
        if (mEmail == null) {
            mEmail = new MutableLiveData<String>();
        }
        return mEmail;
    }

    public MutableLiveData<String> getPassword() {
        if (mPassword == null) {
            mPassword = new MutableLiveData<String>();
        }
        return mPassword;
    }
}

