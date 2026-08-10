package com.scanCrunch.core.exception;

/**
 * Thrown when a JWT is structurally valid but has expired.
 */
public class JwtExpiredException extends RuntimeException {

    public JwtExpiredException(String message) {
        super(message);
    }

    public JwtExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
