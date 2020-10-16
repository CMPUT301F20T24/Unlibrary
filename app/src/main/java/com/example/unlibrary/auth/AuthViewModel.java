package com.example.unlibrary.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {

    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    private MutableLiveData<String> mPassword = new MutableLiveData<>();

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

    public void signUp() {
        System.out.println(mEmail.getValue());
    }

    public void signIn() {
        System.out.println("hello");
        System.out.println(mEmail.getValue());
    }
}

