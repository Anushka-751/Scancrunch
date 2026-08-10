package com.scanCrunch.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.domain.auth.dto.AuthResponse;
import com.scanCrunch.domain.auth.dto.AuthUserResponse;
import com.scanCrunch.domain.auth.dto.EmailAvailabilityResponse;
import com.scanCrunch.domain.auth.dto.JwtValidationRequest;
import com.scanCrunch.domain.auth.dto.JwtValidationResponse;
import com.scanCrunch.domain.auth.dto.LoginRequest;
import com.scanCrunch.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    
    private final AuthService authService;

    /*
     * Manual registration has been moved to:
     *
     * POST /api/auth/register/send-otp
     * POST /api/auth/register/verify-otp
     * POST /api/auth/register/resend-otp
     *
     * Therefore the old direct /register endpoint is removed.
     */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {

        authService.logout();

        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Checks whether an email is available for registration.
     * Public endpoint, intended to be called before Registration.
     */
    @GetMapping("/check-email")
    public ResponseEntity<EmailAvailabilityResponse> checkEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(
                authService.checkEmailAvailability(email));
    }

    /**
     * Validates a JWT's signature, format, and expiration.
     * Public endpoint - returns {"valid": true/false} rather than a
     * 401, since a client may legitimately call this just to check.
     */
    @PostMapping("/validate-token")
    public ResponseEntity<JwtValidationResponse> validateToken(
            @Valid @RequestBody JwtValidationRequest request) {

        boolean valid = authService.validateToken(request.getToken());

        return ResponseEntity.ok(
                JwtValidationResponse.builder()
                        .valid(valid)
                        .build());
    }

    /**
     * Returns the currently authenticated user's identity.
     * Protected route - requires a valid JWT (enforced by
     * SecurityConfig's anyRequest().authenticated()).
     */
    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> me() {

        return ResponseEntity.ok(
                authService.getCurrentAuthUser());
    }
}