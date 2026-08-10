package com.scanCrunch.domain.otp.service;

import com.scanCrunch.domain.otp.entity.EmailOtp;
import com.scanCrunch.domain.otp.payload.GenerateOtpPayload;

public interface EmailOtpService {

    String generateAndSaveOtp(GenerateOtpPayload payload);

    EmailOtp getByEmail(String email);

    boolean verifyOtp(EmailOtp emailOtp, String enteredOtp);

    void deleteByEmail(String email);
}