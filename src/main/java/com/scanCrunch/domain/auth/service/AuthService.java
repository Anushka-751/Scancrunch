package com.scanCrunch.domain.auth.service;

import com.scanCrunch.domain.auth.dto.AuthResponse;
import com.scanCrunch.domain.auth.dto.AuthUserResponse;
import com.scanCrunch.domain.auth.dto.EmailAvailabilityResponse;
import com.scanCrunch.domain.auth.dto.GoogleLoginRequest;
import com.scanCrunch.domain.auth.dto.LoginRequest;
import com.scanCrunch.domain.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse googleLogin(GoogleLoginRequest request);

    void logout();

    /**
     * Checks whether an email is available for registration.
     * Used before Registration by the frontend.
     */
    EmailAvailabilityResponse checkEmailAvailability(String email);

    /**
     * Validates a JWT's signature, format, and expiration.
     * Returns true/false rather than throwing, so the caller (the
     * /validate-token API) can report status without a 401.
     */
    boolean validateToken(String token);

    /**
     * Returns the currently authenticated user's basic identity info.
     * Throws UnauthorizedException if no user is authenticated.
     */
    AuthUserResponse getCurrentAuthUser();
}
