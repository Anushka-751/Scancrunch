package com.scanCrunch.core.exception;

/**
 * Thrown when a request requires authentication but none (or invalid
 * credentials) were provided.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
