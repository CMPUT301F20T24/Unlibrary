package com.example.unlibrary.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.util.SingleLiveEvent;
import com.google.firebase.auth.FirebaseAuth;

public class AuthViewModel extends ViewModel {

    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    private MutableLiveData<String> mPassword = new MutableLiveData<>();
    private SingleLiveEvent<String> mInvalidLoginEvent;
    private SingleLiveEvent<Void> mRegisterNavigationEvent;

    private FirebaseAuth mAuth;

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

    public SingleLiveEvent<Void> getRegisterNavigationEvent() {
        if (mRegisterNavigationEvent == null) {
            mRegisterNavigationEvent = new SingleLiveEvent<Void>();
        }
        return mRegisterNavigationEvent;
    }

    public SingleLiveEvent<String> getInvalidLoginEvent() {
        if (mInvalidLoginEvent == null) {
            mInvalidLoginEvent = new SingleLiveEvent<String>();
        }
        return mInvalidLoginEvent;
    }

    public void createAccount() {
        mRegisterNavigationEvent.call();
    }

    public void login() {
        // Validate data
        if (mEmail.getValue() == null || mEmail.getValue().isEmpty()) {
            mInvalidLoginEvent.setValue("Missing email.");
            return;
        } else if (mPassword.getValue() == null || mPassword.getValue().isEmpty()) {
            mInvalidLoginEvent.setValue("Missing password.");
            return;
        }

        // Try to authenticate the user
        initFirebaseAuth();
        mAuth.signInWithEmailAndPassword(mEmail.getValue(), mPassword.getValue())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login succeeded, navigate away from auth activity
                        // TODO

                    } else {
                        // Login failed, show toast
                        mInvalidLoginEvent.setValue("Invalid email or password.");
                    }
                });
    }

    private void initFirebaseAuth() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
    }

}

