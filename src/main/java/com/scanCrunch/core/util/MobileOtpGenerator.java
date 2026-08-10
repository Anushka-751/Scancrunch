package com.scanCrunch.core.util;

import java.security.SecureRandom;

public class MobileOtpGenerator {

    private static final SecureRandom random = new SecureRandom();

    private MobileOtpGenerator() {
    }

    public static String generateOtp() {

        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }
}