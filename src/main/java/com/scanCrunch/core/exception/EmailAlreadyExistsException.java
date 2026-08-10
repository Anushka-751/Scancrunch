package com.scanCrunch.core.exception;

/**
 * Thrown when an operation requires an email to be unique but it is
 * already registered.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
