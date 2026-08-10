
package com.scanCrunch.domain.auth.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.scanCrunch.core.exception.InvalidMobileOtpException;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.core.exception.UnauthorizedException;
import com.scanCrunch.core.exception.UserAlreadyExistsException;
import com.scanCrunch.core.security.JwtService;
import com.scanCrunch.core.security.SecurityUtils;
import com.scanCrunch.domain.auth.dto.AuthResponse;
import com.scanCrunch.domain.auth.dto.AuthUserResponse;
import com.scanCrunch.domain.auth.dto.EmailAvailabilityResponse;
import com.scanCrunch.domain.auth.dto.GoogleLoginRequest;
import com.scanCrunch.domain.auth.dto.LoginRequest;
import com.scanCrunch.domain.auth.dto.RegisterRequest;
import com.scanCrunch.domain.auth.mapper.UserMapper;
import com.scanCrunch.domain.auth.service.AuthService;
import com.scanCrunch.domain.mobileotp.repository.MobileOtpRepository;
import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.user.enums.Role;
import com.scanCrunch.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MobileOtpRepository mobileOtpRepository;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;

    @Override
    public AuthResponse register(RegisterRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Check duplicate phone number
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new UserAlreadyExistsException("Phone number already exists");
        }

        // Check password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check whether mobile number is verified
        mobileOtpRepository
                .findByPhoneNumberAndVerifiedTrue(request.getPhone())
                .orElseThrow(() -> new InvalidMobileOtpException(
                        "Please verify your mobile number before registration."
                ));

        // Convert request DTO to User entity
        User user = userMapper.toEntity(request);

        // Set default role
        user.setRole(Role.CUSTOMER);

        // Mark phone as verified
        user.setPhoneVerified(true);

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Save user
        User savedUser = userRepository.save(user);

        // Delete OTP after successful registration
        mobileOtpRepository.deleteByPhoneNumber(request.getPhone());

        return AuthResponse.builder()
                .message("Registration Successful")
                .userId(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invalid Email or Password"
                        )
                );

        // Check email verification
        if (!Boolean.TRUE.equals(user.getVerified())) {
            throw new ResourceNotFoundException(
                    "Email is not verified. Please verify your email first."
            );
        }

        // Check password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid Email or Password");
        }

        // Generate JWT
        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .build();
    }

    @Override
    public AuthResponse googleLogin(GoogleLoginRequest request) {

        // Google OAuth flow is handled by OAuth2SuccessHandler
        throw new UnsupportedOperationException(
                "Google login not implemented here"
        );
    }

    @Override
    public void logout() {

        // JWT is stateless.
        // Client removes the token from storage.
    }

    @Override
    public EmailAvailabilityResponse checkEmailAvailability(String email) {

        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();

        boolean exists = userRepository.existsByEmail(normalizedEmail);

        if (exists) {
            return EmailAvailabilityResponse.builder()
                    .available(false)
                    .message("Email already registered.")
                    .build();
        }

        return EmailAvailabilityResponse.builder()
                .available(true)
                .build();
    }

    @Override
    public boolean validateToken(String token) {

        return jwtService.isTokenStructurallyValid(token);
    }

    @Override
    public AuthUserResponse getCurrentAuthUser() {

        User user = securityUtils.getCurrentUser();

        if (user == null) {
            throw new UnauthorizedException("No authenticated user found.");
        }

        return AuthUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}

