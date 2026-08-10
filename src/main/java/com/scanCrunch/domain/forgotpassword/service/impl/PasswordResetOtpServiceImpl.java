package com.scanCrunch.domain.forgotpassword.service.impl;

import java.time.LocalDateTime;
import java.time.Duration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.scanCrunch.core.exception.PasswordResetException;
import com.scanCrunch.core.exception.InvalidResetOtpException;
import com.scanCrunch.core.exception.PasswordMismatchException;

import com.scanCrunch.core.exception.ResetOtpExpiredException;
import com.scanCrunch.core.exception.ResetOtpNotFoundException;
import com.scanCrunch.core.security.OtpRateLimiter;
import com.scanCrunch.core.util.PasswordResetUtil;
import com.scanCrunch.core.util.ResetOtpGenerator;
import com.scanCrunch.domain.email.service.EmailService;
import com.scanCrunch.domain.forgotpassword.entity.PasswordResetOtp;
import com.scanCrunch.domain.forgotpassword.repository.PasswordResetOtpRepository;
import com.scanCrunch.domain.forgotpassword.service.PasswordResetOtpService;
import com.scanCrunch.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.scanCrunch.core.exception.AccountNotFoundException;

import com.scanCrunch.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetOtpServiceImpl implements PasswordResetOtpService {

        private static final String RATE_LIMIT_KEY_PREFIX = "forgot-password-otp:";

        private final PasswordResetOtpRepository otpRepository;
        private final UserRepository userRepository;
        private final EmailService emailService;
        private final BCryptPasswordEncoder passwordEncoder;
        private final OtpRateLimiter otpRateLimiter;

        @Override
        @Transactional
        public void sendOtp(String identifier) {

                // OTP spam protection: max 5 requests per 15 minutes
                otpRateLimiter.checkAndRecord(RATE_LIMIT_KEY_PREFIX + identifier);

                // Check if user exists
                User user = userRepository.findByEmailOrPhone(identifier, identifier)
                                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

                // Generate OTP
                String otp = ResetOtpGenerator.generateOtp();

                // Find existing OTP or create new one
                PasswordResetOtp passwordResetOtp = otpRepository
                                .findByIdentifier(identifier)
                                .orElse(new PasswordResetOtp());

                passwordResetOtp.setIdentifier(identifier);
                passwordResetOtp.setOtp(otp);
                passwordResetOtp.setType(
                                identifier.equalsIgnoreCase(user.getEmail()) ? "EMAIL" : "PHONE");
                passwordResetOtp.setExpiresAt(PasswordResetUtil.getExpiryTime());
                passwordResetOtp.setVerified(false);

                // Initialize only for a new OTP record
                // Initialize values if they are null
                if (passwordResetOtp.getResendCount() == null) {
                        passwordResetOtp.setResendCount(0);
                }

                passwordResetOtp.setBlockedUntil(null);
                passwordResetOtp.setLastSentAt(LocalDateTime.now());

                otpRepository.save(passwordResetOtp);

                emailService.sendEmail(
                                user.getEmail(),
                                "Password Reset OTP",
                                "Your OTP is: " + otp + "\nThis OTP is valid for 5 minutes.");
        }

        @Override
        @Transactional
        public void verifyOtp(String identifier, String otp) {

                PasswordResetOtp passwordResetOtp = otpRepository
                                .findByIdentifierAndOtp(identifier, otp)
                                .orElseThrow(() -> new InvalidResetOtpException("Invalid OTP"));

                if (PasswordResetUtil.isExpired(passwordResetOtp.getExpiresAt())) {

                        otpRepository.delete(passwordResetOtp);

                        throw new ResetOtpExpiredException("OTP has expired");
                }

                passwordResetOtp.setVerified(true);

                otpRepository.save(passwordResetOtp);
        }

        @Override
        @Transactional
        public void resendOtp(String identifier) {

                // OTP spam protection: max 5 requests per 15 minutes
                otpRateLimiter.checkAndRecord(RATE_LIMIT_KEY_PREFIX + identifier);

                PasswordResetOtp passwordResetOtp = otpRepository
                                .findByIdentifier(identifier)
                                .orElseThrow(() -> new ResetOtpNotFoundException("Please request OTP first."));

                LocalDateTime now = LocalDateTime.now();

                // Check 1-hour block
                if (passwordResetOtp.getBlockedUntil() != null
                                && now.isBefore(passwordResetOtp.getBlockedUntil())) {

                        throw new PasswordResetException(
                                        "Resend OTP is disabled. Try again after 1 hour.");
                }

                // Check 30-second cooldown
                // Check 30-second cooldown
                if (passwordResetOtp.getLastSentAt() != null
                                && Duration.between(passwordResetOtp.getLastSentAt(), now).getSeconds() < 30) {

                        throw new PasswordResetException(
                                        "Please wait 30 seconds before requesting another OTP.");
                }

                // Maximum 3 resend attempts
                int resendCount = passwordResetOtp.getResendCount() == null
                                ? 0
                                : passwordResetOtp.getResendCount();

                // Maximum 3 resend attempts
                if (resendCount >= 3) {

                        passwordResetOtp.setBlockedUntil(now.plusHours(1));
                        otpRepository.save(passwordResetOtp);

                        throw new PasswordResetException(
                                        "Maximum resend attempts reached. Try again after 1 hour.");
                }

                User user = userRepository
                                .findByEmailOrPhone(identifier, identifier)
                                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

                String otp = ResetOtpGenerator.generateOtp();

                passwordResetOtp.setOtp(otp);
                passwordResetOtp.setVerified(false);
                passwordResetOtp.setExpiresAt(PasswordResetUtil.getExpiryTime());
                passwordResetOtp.setLastSentAt(now);
                passwordResetOtp.setResendCount(resendCount + 1);

                otpRepository.save(passwordResetOtp);

                emailService.sendEmail(
                                user.getEmail(),
                                "Password Reset OTP",
                                "Your OTP is: " + otp + "\nThis OTP is valid for 5 minutes.");
        }

        @Override
        @Transactional
        public void resetPassword(String identifier,
                        String otp,
                        String newPassword,
                        String confirmPassword) {

                // Check password confirmation
                if (!newPassword.equals(confirmPassword)) {
                        throw new PasswordMismatchException("Passwords do not match");
                }

                // Find verified OTP
                PasswordResetOtp passwordResetOtp = otpRepository
                                .findByIdentifierAndOtpAndVerified(identifier, otp, true)
                                .orElseThrow(() -> new ResetOtpNotFoundException("OTP not verified"));

                // Check OTP expiry
                if (PasswordResetUtil.isExpired(passwordResetOtp.getExpiresAt())) {

                        otpRepository.delete(passwordResetOtp);

                        throw new ResetOtpExpiredException("OTP has expired");
                }

                // Find user
                // Find user
                User user = userRepository
                                .findByEmailOrPhone(identifier, identifier)
                                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

                // Check if the new password is the same as the old password
                if (passwordEncoder.matches(newPassword, user.getPassword())) {
                        throw new PasswordResetException(
                                        "New password cannot be the same as the old password");
                }

                // Encode and save password
                user.setPassword(passwordEncoder.encode(newPassword));

                userRepository.save(user);

                // Delete OTP after successful password reset
                otpRepository.delete(passwordResetOtp);
        }
}