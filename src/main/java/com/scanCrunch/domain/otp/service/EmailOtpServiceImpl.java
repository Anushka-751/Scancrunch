package com.scanCrunch.domain.otp.service;

import com.scanCrunch.core.exception.OtpNotFoundException;
import com.scanCrunch.core.util.OtpGenerator;
import com.scanCrunch.domain.otp.entity.EmailOtp;
import com.scanCrunch.domain.otp.payload.GenerateOtpPayload;
import com.scanCrunch.domain.otp.repository.EmailOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailOtpServiceImpl implements EmailOtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private final EmailOtpRepository emailOtpRepository;
    private final OtpGenerator otpGenerator;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String generateAndSaveOtp(GenerateOtpPayload payload) {

        String email = payload.getEmail()
                .trim()
                .toLowerCase();

        /*
         * Generate new 6-digit OTP.
         */
        String rawOtp = otpGenerator.generateOtp();

        LocalDateTime now = LocalDateTime.now();

        /*
         * Find existing pending registration.
         * If present, replace the old OTP.
         */
        EmailOtp emailOtp = emailOtpRepository
                .findByEmail(email)
                .orElse(new EmailOtp());

        /*
         * Store registration details.
         */
        emailOtp.setEmail(email);
        emailOtp.setFirstName(payload.getFirstName());
        emailOtp.setLastName(payload.getLastName());
        emailOtp.setPhone(payload.getPhone());

        /*
         * Password is already BCrypt-hashed
         * before reaching this service.
         */
        emailOtp.setPasswordHash(payload.getPasswordHash());

        /*
         * Never store the raw OTP.
         * Store BCrypt hash instead.
         */
        emailOtp.setOtp(
                passwordEncoder.encode(rawOtp));

        emailOtp.setCreatedAt(now);
        emailOtp.setLastSentAt(now);

        /*
         * OTP is valid for 5 minutes.
         */
        emailOtp.setExpiresAt(
                now.plusMinutes(OTP_EXPIRY_MINUTES));

        emailOtp.setVerified(false);

        emailOtpRepository.save(emailOtp);

        /*
         * Return raw OTP only so the caller
         * can send it to the user's email.
         *
         * It is NOT stored in the database.
         */
        return rawOtp;
    }

    @Override
    @Transactional(readOnly = true)
    public EmailOtp getByEmail(String email) {

        String normalizedEmail = email
                .trim()
                .toLowerCase();

        return emailOtpRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new OtpNotFoundException(
                        "OTP not found. Please request a new OTP."));
    }

    @Override
    public boolean verifyOtp(
            EmailOtp emailOtp,
            String enteredOtp) {

        return passwordEncoder.matches(
                enteredOtp,
                emailOtp.getOtp());
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {

        emailOtpRepository.deleteByEmail(
                email.trim().toLowerCase());
    }
}