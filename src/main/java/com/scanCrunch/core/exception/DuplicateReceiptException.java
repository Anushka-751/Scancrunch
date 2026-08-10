package com.scanCrunch.core.exception;

public class DuplicateReceiptException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateReceiptException(String message) {
        super(message);
    }
}
