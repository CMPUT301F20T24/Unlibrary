/*
 * AuthViewModel
 *
 * October 18, 2020
 *
 * TODO copyright information
 */

package com.example.unlibrary.auth;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.util.SingleLiveEvent;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Manages the authentication flow business logic. Controls both login and registration.
 */
public class AuthViewModel extends ViewModel {

    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    private MutableLiveData<String> mPassword = new MutableLiveData<>();
    private MutableLiveData<String> mUsername = new MutableLiveData<>();
    private SingleLiveEvent<String> mFailureMsgEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Fragment> mFragmentNavigationEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> mAuthenticatedEvent = new SingleLiveEvent<>();

    private FirebaseAuth mAuth;

    /**
     * Email getter for data binding. Exposes data in a mutable format so 2-way binding works.
     * @return Email MutableLiveData
     */
    public MutableLiveData<String> getEmail() {
        if (mEmail == null) {
            mEmail = new MutableLiveData<>();
        }
        return mEmail;
    }

    /**
     * Password getter for data binding. Exposes data in a mutable format so 2-way binding works.
     * @return Password MutableLiveData
     */
    public MutableLiveData<String> getPassword() {
        if (mPassword == null) {
            mPassword = new MutableLiveData<>();
        }
        return mPassword;
    }

    /**
     * Username getter for data binding. Exposes data in a mutable format so 2-way binding works.
     * @return Useranme MutableLiveData
     */
    public MutableLiveData<String> getUsername() {
        if (mUsername == null) {
            mUsername = new MutableLiveData<>();
        }
        return mUsername;
    }

    /**
     * FragmentNavigationEvent getter for activity observers.
     * @return Event of which fragment to navigate to
     */
    public SingleLiveEvent<Fragment> getFragmentNavigationEvent() {
        if (mFragmentNavigationEvent == null) {
            mFragmentNavigationEvent = new SingleLiveEvent<>();
        }
        return mFragmentNavigationEvent;
    }

    /**
     * FailureMsgEvent getter for activity observers.
     * @return Event of failure message to display
     */
    public SingleLiveEvent<String> getFailureMsgEvent() {
        if (mFailureMsgEvent == null) {
            mFailureMsgEvent = new SingleLiveEvent<>();
        }
        return mFailureMsgEvent;
    }

    /**
     * AuthenticatedEvent getter for activity observers.
     * @return Event representing the user has been authenticated
     */
    public SingleLiveEvent<Void> getAuthenticatedEvent() {
        if (mAuthenticatedEvent == null) {
            mAuthenticatedEvent = new SingleLiveEvent<>();
        }
        return mAuthenticatedEvent;
    }

    /**
     * To be called when the user wants to change to the registration screen.
     */
    public void createAccount() {
        // Clear input values
        mEmail.setValue("");
        mPassword.setValue("");

        // Navigate
        mFragmentNavigationEvent.setValue(new RegisterFragment());
    }

    /**
     * To be called when the user tries to login. Validates the data and accesses firebase auth.
     */
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

    /**
     * To be called when the user wants to navigate to the login screen.
     */
    public void cancel() {
        // Clear input values
        mUsername.setValue("");
        mEmail.setValue("");
        mPassword.setValue("");

        // Navigate
        mFragmentNavigationEvent.setValue(new LoginFragment());
    }

    /**
     * To be called when the user wants to register. Validates data and accesses firebase auth.
     */
    public void register() {
        // Validate data
        if (mUsername.getValue() == null || mUsername.getValue().isEmpty()) {
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

    /**
     * Wrapper to access firebase without hitting null pointer exceptions.
     */
    private void initFirebaseAuth() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
    }
}
