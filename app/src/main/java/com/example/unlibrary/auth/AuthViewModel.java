/*
 * AuthViewModel
 *
 * October 18, 2020
 *
 * Copyright (c) Team 24, Fall2020, CMPUT301, University of Alberta
 */

package com.example.unlibrary.auth;

import android.util.Pair;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlibrary.util.SingleLiveEvent;

/**
 * Manages the authentication flow business logic. Controls both login and registration.
 */
public class AuthViewModel extends ViewModel {

    private MutableLiveData<String> mEmail = new MutableLiveData<>();
    private MutableLiveData<String> mPassword = new MutableLiveData<>();
    private MutableLiveData<String> mUsername = new MutableLiveData<>();
    private SingleLiveEvent<String> mFailureMsgEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Pair<InputKey, String>> mInvalidInputEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Fragment> mFragmentNavigationEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> mAuthenticatedEvent = new SingleLiveEvent<>();

    private final AuthRepository mAuthRepository;

    public enum InputKey {
        EMAIL,
        PASSWORD,
        USERNAME
    }

    public AuthViewModel() {
        this.mAuthRepository = new AuthRepository();
    }

    /**
     * Email getter for data binding. Exposes data in a mutable format so 2-way binding works.
     *
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
     *
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
     *
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
     *
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
     *
     * @return Event of failure message to display
     */
    public SingleLiveEvent<String> getFailureMsgEvent() {
        if (mFailureMsgEvent == null) {
            mFailureMsgEvent = new SingleLiveEvent<>();
        }
        return mFailureMsgEvent;
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
     * AuthenticatedEvent getter for activity observers.
     *
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
        String email = "", password = "";
        boolean invalid = false;
        try {
            email = validateEmail(mEmail.getValue());
            mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, null));
        } catch (InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, e.getMessage()));
            invalid = true;
        }
        try {
            password = validatePassword(mPassword.getValue());
            mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, null));
        } catch (InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, e.getMessage()));
            invalid = true;
        }
        if (invalid) {
            return;
        }

        // Try to authenticate the user
        mAuthRepository.signIn(email, password, (s, msg) -> {
            if (s) {
                // Login succeeded, navigate away from auth activity
                mAuthenticatedEvent.call();
            } else {
                // Login failed, show toast
                mFailureMsgEvent.setValue(msg);
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
        String email = "", password = "", username = "";
        boolean invalid = false;
        try {
            email = validateEmail(mEmail.getValue());
            mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, null));
        } catch (InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.EMAIL, e.getMessage()));
            invalid = true;
        }
        try {
            password = validatePassword(mPassword.getValue());
            mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, null));
        } catch (InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.PASSWORD, e.getMessage()));
            invalid = true;
        }
        try {
            username = validateUsername(mUsername.getValue());
            mInvalidInputEvent.setValue(new Pair<>(InputKey.USERNAME, null));
        } catch (InvalidInputException e) {
            mInvalidInputEvent.setValue(new Pair<>(InputKey.USERNAME, e.getMessage()));
            invalid = true;
        }
        if (invalid) {
            return;
        }

        // Try to register new user
        mAuthRepository.register(email, password, username, (s, msg) -> {
            if (s) {
                // Creation succeeded, navigate away from auth activity
                mAuthenticatedEvent.call();
            } else {
                // Creation failed, show toast
                mFailureMsgEvent.setValue(msg);
            }
        });
    }

    /**
     * Validate an email value.
     * @param email Email to validate
     * @return Validated email
     * @throws InvalidInputException Thrown when email is invalid
     */
    private String validateEmail(String email) throws InvalidInputException {
        if (email == null || email.isEmpty()) {
            throw new InvalidInputException("Email is empty.");
        }

        // Valid email
        // https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if (!email.matches(regex)) {
            throw new InvalidInputException("Email is invalid.");
        }

        return email;
    }

    /**
     * Validate a password value.
     * @param password Password to validate.
     * @return Validated password.
     * @throws InvalidInputException Thrown when password is invalid
     */
    private String validatePassword(String password) throws InvalidInputException {
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password is empty.");
        }

        // Length
        if (password.length() < 6) {
            throw new InvalidInputException("Password is too short.");
        }

        return password;
    }

    /**
     * Validate a username value. Does not ensure that the username is globally unique.
     * @param username Username to validate.
     * @return Validated username
     * @throws InvalidInputException Thrown when username is invalid.
     */
    private String validateUsername(String username) throws InvalidInputException {
        if (username == null || username.isEmpty()) {
            throw new InvalidInputException("Username is empty.");
        }

        // Alphanumeric
        String regex = "[A-Za-z0-9]+";
        if (!username.matches(regex)) {
            throw new InvalidInputException("Username is not alphanumeric");
        }

        return username;
    }

    /**
     * Exception thrown when input data is invalid.
     */
    public static class InvalidInputException extends Exception {
        /**
         * Constructor for exception.
         * @param errorMessage Reason that input is invalid
         */
        public InvalidInputException(String errorMessage) {
            super(errorMessage);
        }
    }
}
