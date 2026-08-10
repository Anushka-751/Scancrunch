package com.scanCrunch.core.util;

public class PasswordValidator {

    private PasswordValidator() {
    }

    public static boolean isValid(String password) {

        if (password == null) {
            return false;
        }

        // Minimum 8 characters
        if (password.length() < 8) {
            return false;
        }

        return true;
    }
}