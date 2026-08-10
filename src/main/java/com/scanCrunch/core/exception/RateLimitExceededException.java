package com.scanCrunch.core.exception;

/**
 * Thrown when a client exceeds the allowed number of OTP requests
 * within the configured time window.
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}
