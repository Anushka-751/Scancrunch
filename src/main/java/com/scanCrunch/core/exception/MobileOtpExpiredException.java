package com.scanCrunch.core.exception;

public class MobileOtpExpiredException extends RuntimeException {

    public MobileOtpExpiredException(String message) {
        super(message);
    }
}