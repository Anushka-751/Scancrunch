package com.scanCrunch.domain.auth.service;

import com.scanCrunch.domain.auth.payload.RegistrationOtpResponse;
import com.scanCrunch.domain.auth.payload.ResendRegistrationOtpRequest;
import com.scanCrunch.domain.auth.payload.SendRegistrationOtpRequest;
import com.scanCrunch.domain.auth.payload.VerifyRegistrationOtpRequest;

public interface RegistrationOtpService {

    RegistrationOtpResponse sendOtp(
            SendRegistrationOtpRequest request);

    RegistrationOtpResponse verifyOtp(
            VerifyRegistrationOtpRequest request);

    RegistrationOtpResponse resendOtp(
            ResendRegistrationOtpRequest request);
}