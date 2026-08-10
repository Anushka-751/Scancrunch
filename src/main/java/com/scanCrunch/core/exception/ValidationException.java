package com.scanCrunch.core.exception;

/**
 * Thrown by services for validation failures that aren't covered by
 * bean-validation annotations (e.g. format checks performed manually).
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
