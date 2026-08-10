package com.scanCrunch.domain.forgotpassword.service;

public interface PasswordResetOtpService {

    void sendOtp(String identifier);

    void resendOtp(String identifier);

    void verifyOtp(String identifier, String otp);


    void resetPassword(String identifier,
            String otp,
            String newPassword,
            String confirmPassword);
}