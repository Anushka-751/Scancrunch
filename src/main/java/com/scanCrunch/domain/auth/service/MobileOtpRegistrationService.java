package com.scanCrunch.domain.auth.service;

import com.scanCrunch.domain.auth.dto.AuthResponse;
import com.scanCrunch.domain.auth.dto.RegisterRequest;

public interface MobileOtpRegistrationService {

    AuthResponse register(RegisterRequest request);

}