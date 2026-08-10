package com.scanCrunch.domain.profile.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scanCrunch.core.security.SecurityUtils;
import com.scanCrunch.domain.forgotpassword.service.PasswordResetOtpService;
import com.scanCrunch.domain.profile.dto.ChangePasswordRequest;
import com.scanCrunch.domain.profile.dto.EmailOtpRequest;
import com.scanCrunch.domain.profile.dto.MobileOtpRequest;
import com.scanCrunch.domain.profile.dto.ProfileResponse;
import com.scanCrunch.domain.profile.dto.SendPasswordOtpRequest;
import com.scanCrunch.domain.profile.dto.UpdateProfileRequest;
import com.scanCrunch.domain.profile.dto.VerifyEmailOtpRequest;
import com.scanCrunch.domain.profile.dto.VerifyMobileOtpRequest;
import com.scanCrunch.domain.profile.dto.VerifyPasswordOtpRequest;
import com.scanCrunch.domain.profile.mapper.ProfileMapper;
import com.scanCrunch.domain.profile.service.ProfileService;
import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final PasswordResetOtpService passwordResetOtpService;
    private final PasswordEncoder passwordEncoder;
    private final com.scanCrunch.domain.forgotpassword.repository.PasswordResetOtpRepository otpRepository;
    
    private final com.scanCrunch.domain.otp.service.EmailOtpService emailOtpService;
    private final com.scanCrunch.domain.mobileotp.service.MobileOtpService mobileOtpService;
    private final com.scanCrunch.core.util.EmailSenderUtil emailSenderUtil;

    private User getCurrentUserOrThrow() {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        return currentUser;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        User currentUser = getCurrentUserOrThrow();
        User user = userRepository.findById(currentUser.getId())
                .orElse(currentUser);
        return profileMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        User currentUser = getCurrentUserOrThrow();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setFullName(request.getFirstName().trim() + " " + request.getLastName().trim());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void sendEmailOtp(EmailOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User currentUser = getCurrentUserOrThrow();
        com.scanCrunch.domain.otp.payload.GenerateOtpPayload payload = com.scanCrunch.domain.otp.payload.GenerateOtpPayload.builder()
                .email(email)
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .phone(currentUser.getPhone() != null ? currentUser.getPhone() : "")
                .passwordHash(currentUser.getPassword() != null ? currentUser.getPassword() : "")
                .build();

        String rawOtp = emailOtpService.generateAndSaveOtp(payload);

        try {
            emailSenderUtil.sendRegistrationOtp(email, currentUser.getFirstName(), rawOtp);
        } catch (Exception e) {
            emailOtpService.deleteByEmail(email);
            throw new RuntimeException("Unable to send OTP. Please try again.");
        }
    }

    @Override
    @Transactional
    public void verifyEmailOtp(VerifyEmailOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        
        com.scanCrunch.domain.otp.entity.EmailOtp emailOtp = emailOtpService.getByEmail(email);
        if (emailOtp.getExpiresAt() == null || java.time.LocalDateTime.now().isAfter(emailOtp.getExpiresAt())) {
            throw new com.scanCrunch.core.exception.OtpExpiredException("OTP expired. Please resend OTP.");
        }

        boolean valid = emailOtpService.verifyOtp(emailOtp, request.getOtp().trim());
        if (!valid) {
            throw new com.scanCrunch.core.exception.InvalidOtpException("Invalid OTP.");
        }

        User currentUser = getCurrentUserOrThrow();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setEmail(email);
        user.setVerifiedEmail(true);
        userRepository.save(user);

        emailOtpService.deleteByEmail(email);
    }

    @Override
    @Transactional
    public void sendMobileOtp(MobileOtpRequest request) {
        String phone = request.getPhone().trim();
        mobileOtpService.sendOtp(phone);
    }

    @Override
    @Transactional
    public void verifyMobileOtp(VerifyMobileOtpRequest request) {
        String phone = request.getPhone().trim();
        mobileOtpService.verifyOtp(phone, request.getOtp().trim());

        User currentUser = getCurrentUserOrThrow();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setPhone(phone);
        user.setVerifiedPhone(true);
        user.setPhoneVerified(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void sendChangePasswordOtp(SendPasswordOtpRequest request) {
        User currentUser = getCurrentUserOrThrow();
        String identifier;
        if ("MOBILE".equalsIgnoreCase(request.getType())) {
            identifier = currentUser.getPhone();
            if (identifier == null || identifier.isBlank()) {
                throw new IllegalArgumentException("No registered phone number found for current user");
            }
        } else {
            identifier = currentUser.getEmail();
            if (identifier == null || identifier.isBlank()) {
                throw new IllegalArgumentException("No registered email found for current user");
            }
        }
        passwordResetOtpService.sendOtp(identifier);
    }

    @Override
    @Transactional
    public void verifyChangePasswordOtp(VerifyPasswordOtpRequest request) {
        User currentUser = getCurrentUserOrThrow();
        String email = currentUser.getEmail();
        String phone = currentUser.getPhone();
        boolean verified = false;

        if (email != null && !email.isBlank()) {
            try {
                passwordResetOtpService.verifyOtp(email, request.getOtp().trim());
                verified = true;
            } catch (Exception e) {
                if (phone == null || phone.isBlank()) {
                    throw e;
                }
            }
        }

        if (!verified && phone != null && !phone.isBlank()) {
            passwordResetOtpService.verifyOtp(phone, request.getOtp().trim());
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User currentUser = getCurrentUserOrThrow();

        // Validation: OTP must already be verified
        boolean hasVerifiedOtp = false;
        String verifiedIdentifier = null;

        if (currentUser.getEmail() != null) {
            hasVerifiedOtp = otpRepository.findByIdentifier(currentUser.getEmail())
                    .map(com.scanCrunch.domain.forgotpassword.entity.PasswordResetOtp::getVerified)
                    .orElse(false);
            if (hasVerifiedOtp) {
                verifiedIdentifier = currentUser.getEmail();
            }
        }

        if (!hasVerifiedOtp && currentUser.getPhone() != null) {
            hasVerifiedOtp = otpRepository.findByIdentifier(currentUser.getPhone())
                    .map(com.scanCrunch.domain.forgotpassword.entity.PasswordResetOtp::getVerified)
                    .orElse(false);
            if (hasVerifiedOtp) {
                verifiedIdentifier = currentUser.getPhone();
            }
        }

        if (!hasVerifiedOtp) {
            throw new IllegalArgumentException("OTP must be verified first");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Delete verified OTP record after successful password change
        if (verifiedIdentifier != null) {
            otpRepository.deleteByIdentifier(verifiedIdentifier);
        }
    }
}
