package com.scanCrunch.core.exception;

public class PasswordResetException extends RuntimeException {

    public PasswordResetException(String message) {
        super(message);
    }
}