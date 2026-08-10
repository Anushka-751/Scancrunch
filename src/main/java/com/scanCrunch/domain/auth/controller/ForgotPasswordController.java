package com.scanCrunch.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.domain.auth.dto.ResendResetOtpRequest;
import com.scanCrunch.domain.forgotpassword.dto.ResetPasswordRequest;
import com.scanCrunch.domain.forgotpassword.dto.SendOtpRequest;
import com.scanCrunch.domain.forgotpassword.dto.VerifyOtpRequest;
import com.scanCrunch.domain.forgotpassword.service.PasswordResetOtpService;
import com.scanCrunch.domain.payment.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {

        private final PasswordResetOtpService passwordResetOtpService;

        @PostMapping("/forgot-password")
        public ResponseEntity<ApiResponse<Object>> sendOtp(
                        @Valid @RequestBody SendOtpRequest request) {

                passwordResetOtpService.sendOtp(request.getIdentifier());

                return ResponseEntity.ok(
                                new ApiResponse<>(true, "OTP sent successfully", null));
        }

        @PostMapping("/verify-otp")
        public ResponseEntity<ApiResponse<Object>> verifyOtp(
                        @Valid @RequestBody VerifyOtpRequest request) {

                passwordResetOtpService.verifyOtp(
                                request.getIdentifier(),
                                request.getOtp());

                return ResponseEntity.ok(
                                new ApiResponse<>(true, "OTP verified successfully", null));
        }

        @PostMapping("/reset-password")
        public ResponseEntity<ApiResponse<Object>> resetPassword(
                        @Valid @RequestBody ResetPasswordRequest request) {

                passwordResetOtpService.resetPassword(
                                request.getIdentifier(),
                                request.getOtp(),
                                request.getNewPassword(),
                                request.getConfirmPassword());

                return ResponseEntity.ok(
                                new ApiResponse<>(true, "Password reset successful", null));
        }

        @PostMapping("/forgot-password/resend-otp")
        public ResponseEntity<ApiResponse<Object>> resendOtp(
                        @Valid @RequestBody ResendResetOtpRequest request) {

                passwordResetOtpService.resendOtp(request.getIdentifier());

                return ResponseEntity.ok(
                                new ApiResponse<>(true, "OTP resent successfully", null));
        }
}