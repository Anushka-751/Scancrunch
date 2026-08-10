package com.scanCrunch.core.exception;

/**
 * Thrown when a JWT is malformed, has an invalid signature, has been
 * tampered with, or otherwise fails structural/format validation.
 */
public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(String message) {
        super(message);
    }

    public InvalidJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
