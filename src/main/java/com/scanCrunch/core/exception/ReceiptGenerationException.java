package com.scanCrunch.core.exception;

public class ReceiptGenerationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReceiptGenerationException(String message) {
        super(message);
    }

    public ReceiptGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
