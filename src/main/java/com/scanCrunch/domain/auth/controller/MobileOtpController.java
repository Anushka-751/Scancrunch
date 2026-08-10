package com.scanCrunch.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.scanCrunch.domain.auth.dto.MobileOtpResponse;
import com.scanCrunch.domain.auth.dto.ResendMobileOtpRequest;
import com.scanCrunch.domain.auth.dto.SendMobileOtpRequest;
import com.scanCrunch.domain.auth.dto.VerifyMobileOtpRequest;
import com.scanCrunch.domain.mobileotp.service.MobileOtpService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/mobile")
@RequiredArgsConstructor
public class MobileOtpController {

    private final MobileOtpService mobileOtpService;

    @PostMapping("/send-otp")
    public ResponseEntity<MobileOtpResponse> sendOtp(
            @Valid @RequestBody SendMobileOtpRequest request) {

        mobileOtpService.sendOtp(request.getPhoneNumber());

        return ResponseEntity.ok(
                new MobileOtpResponse(true, "OTP sent successfully."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<MobileOtpResponse> verifyOtp(
            @Valid @RequestBody VerifyMobileOtpRequest request) {

        mobileOtpService.verifyOtp(
                request.getPhoneNumber(),
                request.getOtp());

        return ResponseEntity.ok(
                new MobileOtpResponse(true, "Mobile number verified successfully."));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<MobileOtpResponse> resendOtp(
            @Valid @RequestBody ResendMobileOtpRequest request) {

        mobileOtpService.resendOtp(request.getPhoneNumber());

        return ResponseEntity.ok(
                new MobileOtpResponse(true, "New OTP sent successfully."));
    }
}