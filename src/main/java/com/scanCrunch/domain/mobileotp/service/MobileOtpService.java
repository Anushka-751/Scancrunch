package com.scanCrunch.domain.mobileotp.service;

public interface MobileOtpService {

    void sendOtp(String phoneNumber);

    boolean verifyOtp(String phoneNumber, String otp);

    void resendOtp(String phoneNumber);
}