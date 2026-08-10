package com.scanCrunch.domain.mobileotp.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.scanCrunch.core.exception.DuplicateMobileException;
import com.scanCrunch.core.exception.InvalidMobileOtpException;
import com.scanCrunch.core.exception.InvalidPhoneNumberException;
import com.scanCrunch.core.exception.MobileOtpExpiredException;
import com.scanCrunch.core.security.OtpRateLimiter;
import com.scanCrunch.core.util.MobileOtpGenerator;
import com.scanCrunch.core.util.PhoneNumberValidator;
import com.scanCrunch.core.util.SmsSenderUtil;
import com.scanCrunch.domain.mobileotp.entity.MobileOtp;
import com.scanCrunch.domain.mobileotp.repository.MobileOtpRepository;
import com.scanCrunch.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MobileOtpServiceImpl implements MobileOtpService {

    private static final String RATE_LIMIT_KEY_PREFIX = "mobile-otp:";

    private final MobileOtpRepository mobileOtpRepository;
    private final UserRepository userRepository;
    private final SmsSenderUtil smsSenderUtil;
    private final OtpRateLimiter otpRateLimiter;

    @Override
    public void sendOtp(String phoneNumber) {

        // OTP spam protection: max 5 requests per 15 minutes
        otpRateLimiter.checkAndRecord(RATE_LIMIT_KEY_PREFIX + phoneNumber);

        // Validate phone number
        if (!PhoneNumberValidator.isValid(phoneNumber)) {
            throw new InvalidPhoneNumberException("Invalid phone number.");
        }

        // Check duplicate user
        if (userRepository.existsByPhone(phoneNumber)) {
            throw new DuplicateMobileException("Phone number already registered.");
        }

        // Delete previous OTP if exists
        mobileOtpRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(mobileOtpRepository::delete);

        // Generate OTP
        String otp = MobileOtpGenerator.generateOtp();

        // Create entity
        MobileOtp mobileOtp = new MobileOtp();
        mobileOtp.setPhoneNumber(phoneNumber);
        mobileOtp.setOtp(otp);
        mobileOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        mobileOtp.setVerified(false);

        // Save OTP
        mobileOtpRepository.save(mobileOtp);

        // Send SMS
        smsSenderUtil.sendOtp(phoneNumber, otp);
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String otp) {

        MobileOtp mobileOtp = mobileOtpRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new InvalidMobileOtpException("OTP not found."));

        // Check expiry
        if (mobileOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new MobileOtpExpiredException("OTP expired.");
        }

        // Check OTP
        if (!mobileOtp.getOtp().equals(otp)) {
            throw new InvalidMobileOtpException("Invalid OTP.");
        }

        mobileOtp.setVerified(true);
        mobileOtpRepository.save(mobileOtp);

        return true;
    }

    @Override
    public void resendOtp(String phoneNumber) {

        // Delete old OTP
        mobileOtpRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(mobileOtpRepository::delete);

        // Send new OTP
        sendOtp(phoneNumber);
    }
}