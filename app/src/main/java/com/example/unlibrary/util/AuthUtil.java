package com.example.unlibrary.util;

public class AuthUtil {

    /**
     * Validate an email value.
     *
     * @param email Email to validate
     * @return Validated email
     * @throws InvalidInputException Thrown when email is invalid
     */
    public static String validateEmail(String email) throws InvalidInputException {
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
     *
     * @param password Password to validate.
     * @return Validated password.
     * @throws InvalidInputException Thrown when password is invalid
     */
    public static String validatePassword(String password) throws InvalidInputException {
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
     *
     * @param username Username to validate.
     * @return Validated username
     * @throws InvalidInputException Thrown when username is invalid.
     */
    public static String validateUsername(String username) throws InvalidInputException {
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
         *
         * @param errorMessage Reason that input is invalid
         */
        public InvalidInputException(String errorMessage) {
            super(errorMessage);
        }
    }
}
