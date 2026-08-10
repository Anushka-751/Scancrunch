package com.scanCrunch.domain.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.domain.profile.dto.ChangePasswordRequest;
import com.scanCrunch.domain.profile.dto.EmailOtpRequest;
import com.scanCrunch.domain.profile.dto.MobileOtpRequest;
import com.scanCrunch.domain.profile.dto.ProfileResponse;
import com.scanCrunch.domain.profile.dto.SendPasswordOtpRequest;
import com.scanCrunch.domain.profile.dto.UpdateProfileRequest;
import com.scanCrunch.domain.profile.dto.VerifyEmailOtpRequest;
import com.scanCrunch.domain.profile.dto.VerifyMobileOtpRequest;
import com.scanCrunch.domain.profile.dto.VerifyPasswordOtpRequest;
import com.scanCrunch.domain.profile.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // GET /api/v1/profile
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    // PUT /api/v1/profile
    @PutMapping
    public ResponseEntity<java.util.Map<String, String>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        profileService.updateProfile(request);
        return ResponseEntity.ok(java.util.Map.of("message", "Profile updated successfully"));
    }

    // POST /api/v1/profile/email/send-otp
    @PostMapping("/email/send-otp")
    public ResponseEntity<java.util.Map<String, String>> sendEmailOtp(
            @Valid @RequestBody EmailOtpRequest request) {
        profileService.sendEmailOtp(request);
        return ResponseEntity.ok(java.util.Map.of("message", "OTP sent successfully"));
    }

    // POST /api/v1/profile/email/verify
    @PostMapping("/email/verify")
    public ResponseEntity<java.util.Map<String, String>> verifyEmailOtp(
            @Valid @RequestBody VerifyEmailOtpRequest request) {
        profileService.verifyEmailOtp(request);
        return ResponseEntity.ok(java.util.Map.of("message", "Email updated successfully"));
    }

    // POST /api/v1/profile/mobile/send-otp
    @PostMapping("/mobile/send-otp")
    public ResponseEntity<java.util.Map<String, String>> sendMobileOtp(
            @Valid @RequestBody MobileOtpRequest request) {
        profileService.sendMobileOtp(request);
        return ResponseEntity.ok(java.util.Map.of("message", "OTP sent successfully"));
    }

    // POST /api/v1/profile/mobile/verify
    @PostMapping("/mobile/verify")
    public ResponseEntity<java.util.Map<String, String>> verifyMobileOtp(
            @Valid @RequestBody VerifyMobileOtpRequest request) {
        profileService.verifyMobileOtp(request);
        return ResponseEntity.ok(java.util.Map.of("message", "Mobile updated successfully"));
    }

    // POST /api/v1/profile/change-password/send-otp
    @PostMapping("/change-password/send-otp")
    public ResponseEntity<java.util.Map<String, String>> sendChangePasswordOtp(
            @Valid @RequestBody SendPasswordOtpRequest request) {
        profileService.sendChangePasswordOtp(request);
        return ResponseEntity.ok(java.util.Map.of("message", "OTP sent successfully"));
    }

    // POST /api/v1/profile/change-password/verify-otp
    @PostMapping("/change-password/verify-otp")
    public ResponseEntity<java.util.Map<String, String>> verifyChangePasswordOtp(
            @Valid @RequestBody VerifyPasswordOtpRequest request) {
        profileService.verifyChangePasswordOtp(request);
        return ResponseEntity.ok(java.util.Map.of("message", "OTP verified successfully"));
    }

    // PUT /api/v1/profile/change-password
    @PutMapping("/change-password")
    public ResponseEntity<java.util.Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        profileService.changePassword(request);
        return ResponseEntity.ok(java.util.Map.of("message", "Password changed successfully"));
    }
}
