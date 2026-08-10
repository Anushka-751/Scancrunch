package com.scanCrunch.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.domain.auth.payload.RegistrationOtpResponse;
import com.scanCrunch.domain.auth.payload.ResendRegistrationOtpRequest;
import com.scanCrunch.domain.auth.payload.SendRegistrationOtpRequest;
import com.scanCrunch.domain.auth.payload.VerifyRegistrationOtpRequest;
import com.scanCrunch.domain.auth.service.RegistrationOtpService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/register")
@RequiredArgsConstructor
public class RegistrationOtpController {

    private final RegistrationOtpService registrationOtpService;

    @PostMapping("/send-otp")
    public ResponseEntity<RegistrationOtpResponse> sendOtp(
            @Valid @RequestBody SendRegistrationOtpRequest request) {

        RegistrationOtpResponse response = registrationOtpService.sendOtp(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<RegistrationOtpResponse> verifyOtp(
            @Valid @RequestBody VerifyRegistrationOtpRequest request) {

        RegistrationOtpResponse response = registrationOtpService.verifyOtp(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<RegistrationOtpResponse> resendOtp(
            @Valid @RequestBody ResendRegistrationOtpRequest request) {

        RegistrationOtpResponse response = registrationOtpService.resendOtp(request);

        return ResponseEntity.ok(response);
    }
}