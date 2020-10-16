package com.example.unlibrary.auth;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.util.SingleLiveEvent;

import com.google.firebase.auth.FirebaseAuth;

public class AuthViewModel extends ViewModel {

    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    private MutableLiveData<String> mPassword = new MutableLiveData<>();
    private MutableLiveData<String> mUsername = new MutableLiveData<>();
    private SingleLiveEvent<String> mFailureMsgEvent;
    private SingleLiveEvent<Fragment> mFragmentNavigationEvent;
    private SingleLiveEvent<Void> mAuthenticatedEvent;

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

    public MutableLiveData<String> getUsername() {
        if (mUsername == null) {
            mUsername = new MutableLiveData<String>();
        }
        return mUsername;
    }

    public SingleLiveEvent<Fragment> getFragmentNavigationEvent() {
        if (mFragmentNavigationEvent == null) {
            mFragmentNavigationEvent = new SingleLiveEvent<Fragment>();
        }
        return mFragmentNavigationEvent;
    }

    public SingleLiveEvent<String> getFailureMsgEvent() {
        if (mFailureMsgEvent == null) {
            mFailureMsgEvent = new SingleLiveEvent<String>();
        }
        return mFailureMsgEvent;
    }

    public SingleLiveEvent<Void> getAuthenticatedEvent() {
        if (mAuthenticatedEvent == null) {
            mAuthenticatedEvent = new SingleLiveEvent<Void>();
        }
        return mAuthenticatedEvent;
    }

    public void createAccount() {
        // Clear input values
        mEmail.setValue("");
        mPassword.setValue("");

        // Navigate
        mFragmentNavigationEvent.setValue(new RegisterFragment());
    }

    public void login() {
        // Validate data
        if (mEmail.getValue() == null || mEmail.getValue().isEmpty()) {
            mFailureMsgEvent.setValue("Missing email.");
            return;
        } else if (mPassword.getValue() == null || mPassword.getValue().isEmpty()) {
            mFailureMsgEvent.setValue("Missing password.");
            return;
        }

        // TODO extract to repository
        // Try to authenticate the user
        initFirebaseAuth();
        mAuth.signInWithEmailAndPassword(mEmail.getValue(), mPassword.getValue())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login succeeded, navigate away from auth activity
                        mAuthenticatedEvent.call();

                    } else {
                        // Login failed, show toast
                        mFailureMsgEvent.setValue("Invalid email or password.");
                    }
                });
    }

    public void cancel() {
        // Clear input values
        mUsername.setValue("");
        mEmail.setValue("");
        mPassword.setValue("");

        // Navigate
        mFragmentNavigationEvent.setValue(new LoginFragment());
    }

    public void register() {
        // Validate data
        if (mUsername.getValue() == null || mEmail.getValue().isEmpty()) {
            // TODO ensure that the username is globally unique
            // TODO actually do something with the username
            mFailureMsgEvent.setValue("Missing username");
            return;
        } else if (mEmail.getValue() == null || mEmail.getValue().isEmpty()) {
            mFailureMsgEvent.setValue("Missing email.");
            return;
        } else if (mPassword.getValue() == null || mPassword.getValue().isEmpty()) {
            mFailureMsgEvent.setValue("Missing password.");
            return;
        }

        // TODO extract to repository
        // Try to register new user
        initFirebaseAuth();
        mAuth.createUserWithEmailAndPassword(mEmail.getValue(), mPassword.getValue())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Creation succeeded, navigate away from auth activity
                        mAuthenticatedEvent.call();
                    } else {
                        // Creation failed, show toast
                        mFailureMsgEvent.setValue("Failed to create account.");
                    }
                });
    }

    private void initFirebaseAuth() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
    }
}
