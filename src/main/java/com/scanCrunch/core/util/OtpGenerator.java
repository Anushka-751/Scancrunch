package com.scanCrunch.core.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private static final int OTP_LENGTH = 6;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp() {

        int min = 100000;
        int max = 999999;

        int otp = secureRandom.nextInt(max - min + 1) + min;

        return String.valueOf(otp);
    }
}