package com.scanCrunch.core.util;

import org.springframework.stereotype.Component;

@Component
public class SmsSenderUtil {

    public void sendOtp(String phoneNumber, String otp) {

        System.out.println("--------------------------------");
        System.out.println("SMS OTP");
        System.out.println("Phone : " + phoneNumber);
        System.out.println("OTP   : " + otp);
        System.out.println("--------------------------------");
    }
}