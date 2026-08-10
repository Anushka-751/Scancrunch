package com.scanCrunch.domain.auth.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scanCrunch.core.exception.DuplicateEmailException;
import com.scanCrunch.core.exception.InvalidOtpException;
import com.scanCrunch.core.exception.OtpExpiredException;
import com.scanCrunch.core.exception.RegistrationException;
import com.scanCrunch.core.util.EmailSenderUtil;
import com.scanCrunch.domain.auth.payload.RegistrationOtpResponse;
import com.scanCrunch.domain.auth.payload.ResendRegistrationOtpRequest;
import com.scanCrunch.domain.auth.payload.SendRegistrationOtpRequest;
import com.scanCrunch.domain.auth.payload.VerifyRegistrationOtpRequest;
import com.scanCrunch.domain.auth.service.RegistrationOtpService;
import com.scanCrunch.domain.otp.entity.EmailOtp;
import com.scanCrunch.domain.otp.payload.GenerateOtpPayload;
import com.scanCrunch.domain.otp.service.EmailOtpService;
import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.user.enums.Role;
import com.scanCrunch.domain.user.repository.UserRepository;
import com.scanCrunch.core.exception.OtpNotFoundException;
import com.scanCrunch.core.security.OtpRateLimiter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationOtpServiceImpl
                implements RegistrationOtpService {

        private static final long RESEND_COOLDOWN_SECONDS = 60;
        private static final String RATE_LIMIT_KEY_PREFIX = "registration-otp:";

        private final UserRepository userRepository;
        private final EmailOtpService emailOtpService;
        private final EmailSenderUtil emailSenderUtil;
        private final BCryptPasswordEncoder passwordEncoder;
        private final OtpRateLimiter otpRateLimiter;

        private String normalizeEmail(String email) {
                return email.trim().toLowerCase();
        }

        @Override
        @Transactional
        public RegistrationOtpResponse sendOtp(
                        SendRegistrationOtpRequest request) {

                String email = normalizeEmail(request.getEmail());

                // OTP spam protection: max 5 requests per 15 minutes
                otpRateLimiter.checkAndRecord(RATE_LIMIT_KEY_PREFIX + email);

                // Check duplicate email
                if (userRepository.existsByEmail(email)) {
                        throw new DuplicateEmailException(
                                        "Email already registered.");
                }

                // Check duplicate phone
                if (userRepository.existsByPhone(request.getPhone())) {
                        throw new RegistrationException(
                                        "Phone number already registered.");
                }

                // BCrypt password before storing pending registration
                String passwordHash = passwordEncoder.encode(request.getPassword());

                GenerateOtpPayload payload = GenerateOtpPayload.builder()
                                .email(email)
                                .firstName(request.getFirstName().trim())
                                .lastName(request.getLastName().trim())
                                .phone(request.getPhone().trim())
                                .passwordHash(passwordHash)
                                .build();

                // Generate and save OTP
                String rawOtp = emailOtpService.generateAndSaveOtp(payload);

                // Send email
                try {

                        emailSenderUtil.sendRegistrationOtp(
                                        email,
                                        request.getFirstName().trim(),
                                        rawOtp);

                } catch (Exception e) {

                        e.printStackTrace();

                        emailOtpService.deleteByEmail(email);

                        throw new RegistrationException(
                                        "Unable to send OTP. Please try again.");
                }

                return new RegistrationOtpResponse(
                                true,
                                "OTP sent successfully.");
        }

        @Override
        @Transactional
        public RegistrationOtpResponse verifyOtp(
                        VerifyRegistrationOtpRequest request) {

                String email = normalizeEmail(request.getEmail());

                // Find pending OTP
                EmailOtp emailOtp = emailOtpService.getByEmail(email);

                // Check expiry
                if (emailOtp.getExpiresAt() == null ||
                                LocalDateTime.now()
                                                .isAfter(emailOtp.getExpiresAt())) {

                        throw new OtpExpiredException(
                                        "OTP expired. Please resend OTP.");
                }

                // Check OTP
                String enteredOtp = request.getOtp().trim();

                boolean valid = emailOtpService.verifyOtp(
                                emailOtp,
                                enteredOtp);

                if (!valid) {
                        throw new InvalidOtpException(
                                        "Invalid OTP.");
                }

                // Check duplicate again
                if (userRepository.existsByEmail(email)) {

                        emailOtpService.deleteByEmail(email);

                        throw new DuplicateEmailException(
                                        "Email already registered.");
                }

                // Create actual user only after successful OTP verification
                User user = new User();

                user.setFirstName(emailOtp.getFirstName());
                user.setLastName(emailOtp.getLastName());
                user.setEmail(emailOtp.getEmail());
                user.setPhone(emailOtp.getPhone());

                // Already BCrypt hashed
                user.setPassword(emailOtp.getPasswordHash());

                user.setRole(Role.CUSTOMER);
                user.setActive(true);

                // Email successfully verified
                user.setVerified(true);

                userRepository.save(user);

                // OTP is no longer needed
                emailOtpService.deleteByEmail(email);

                return new RegistrationOtpResponse(
                                true,
                                "Registration completed successfully.");
        }

        @Override
        @Transactional
        public RegistrationOtpResponse resendOtp(
                        ResendRegistrationOtpRequest request) {

                String email = normalizeEmail(request.getEmail());

                // OTP spam protection: max 5 requests per 15 minutes
                otpRateLimiter.checkAndRecord(RATE_LIMIT_KEY_PREFIX + email);

                /*
                 * Find pending registration.
                 *
                 * If there is no EmailOtp record, there is no
                 * registration waiting for OTP verification.
                 */
                EmailOtp emailOtp;

                try {
                        emailOtp = emailOtpService.getByEmail(email);
                } catch (OtpNotFoundException ex) {
                        throw new RegistrationException(
                                        "No pending registration found. Please register first.");
                }

                /*
                 * Spam protection.
                 */
                LocalDateTime now = LocalDateTime.now();

                long secondsSinceLastSend = Duration.between(
                                emailOtp.getLastSentAt(),
                                now).getSeconds();

                if (secondsSinceLastSend < RESEND_COOLDOWN_SECONDS) {

                        long remainingSeconds = RESEND_COOLDOWN_SECONDS
                                        - secondsSinceLastSend;

                        throw new RegistrationException(
                                        "Please wait "
                                                        + remainingSeconds
                                                        + " seconds before requesting another OTP.");
                }

                /*
                 * Generate new OTP.
                 *
                 * This replaces the previous OTP hash and
                 * resets the 5-minute expiry.
                 */
                GenerateOtpPayload payload = GenerateOtpPayload.builder()
                                .email(emailOtp.getEmail())
                                .firstName(emailOtp.getFirstName())
                                .lastName(emailOtp.getLastName())
                                .phone(emailOtp.getPhone())
                                .passwordHash(emailOtp.getPasswordHash())
                                .build();

                String newRawOtp = emailOtpService.generateAndSaveOtp(payload);

                /*
                 * Send new OTP.
                 */
                try {

                        emailSenderUtil.sendRegistrationOtp(
                                        emailOtp.getEmail(),
                                        emailOtp.getFirstName(),
                                        newRawOtp);

                } catch (Exception e) {

                        e.printStackTrace();

                        throw new RegistrationException(
                                        "Unable to send OTP. Please try again.");
                }

                return new RegistrationOtpResponse(
                                true,
                                "OTP sent successfully.");
        }
}