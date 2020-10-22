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

    private AuthRepository mAuthRepository;
    private FirebaseAuth mAuth;

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
        String email, password;
        try {
            email = validateEmail(mEmail.getValue());
        } catch (InvalidInputException e) {
            mFailureMsgEvent.setValue(e.getMessage());
            return;
        }
        try {
            password = validatePassword(mPassword.getValue());
        } catch (InvalidInputException e) {
            mFailureMsgEvent.setValue(e.getMessage());
            return;
        }

        // Try to authenticate the user
        mAuthRepository.signIn(mEmail.getValue(), mPassword.getValue(), task -> {
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
        String email, password, username;
        try {
            email = validateEmail(mEmail.getValue());
        } catch (InvalidInputException e) {
            mFailureMsgEvent.setValue(e.getMessage());
            return;
        }
        try {
            password = validatePassword(mPassword.getValue());
        } catch (InvalidInputException e) {
            mFailureMsgEvent.setValue(e.getMessage());
            return;
        }
        try {
            username = validateUsername(mUsername.getValue());
        } catch (InvalidInputException e) {
            mFailureMsgEvent.setValue(e.getMessage());
            return;
        }

        // Try to register new user
        mAuthRepository.register(mEmail.getValue(), mPassword.getValue(), mUsername.getValue(), (s, msg) -> {
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
        if (email == null) {
            throw new InvalidInputException("Email is null.");
        } else if (email.isEmpty()) {
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
        if (password == null) {
            throw new InvalidInputException("Password is null.");
        } else if (password.isEmpty()) {
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
        if (username == null) {
            throw new InvalidInputException("Username is null.");
        } else if (username.isEmpty()) {
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
