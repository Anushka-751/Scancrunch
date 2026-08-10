package com.scanCrunch.domain.auth.service.impl;

import org.springframework.stereotype.Service;

import com.scanCrunch.domain.auth.dto.AuthResponse;
import com.scanCrunch.domain.auth.dto.RegisterRequest;
import com.scanCrunch.domain.auth.service.AuthService;
import com.scanCrunch.domain.auth.service.MobileOtpRegistrationService;
import com.scanCrunch.domain.mobileotp.entity.MobileOtp;
import com.scanCrunch.domain.mobileotp.repository.MobileOtpRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MobileOtpRegistrationServiceImpl implements MobileOtpRegistrationService {

    private final AuthService authService;
    private final MobileOtpRepository mobileOtpRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {

        MobileOtp mobileOtp = mobileOtpRepository
                .findByPhoneNumberAndVerifiedTrue(request.getPhone())
                .orElseThrow(() ->
                        new RuntimeException("Please verify your mobile number before registration."));

        return authService.register(request);
    }
}