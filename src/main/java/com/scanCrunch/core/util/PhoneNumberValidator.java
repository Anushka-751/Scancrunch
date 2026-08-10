package com.scanCrunch.core.util;

public class PhoneNumberValidator {

    private PhoneNumberValidator() {
    }

    public static boolean isValid(String phone) {

        return phone.matches("^\\+?[0-9]{10,13}$");
    }
}