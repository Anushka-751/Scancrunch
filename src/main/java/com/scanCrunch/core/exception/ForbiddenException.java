package com.scanCrunch.core.exception;

/**
 * Thrown when an authenticated user does not have permission to
 * perform the requested action.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
