package com.scanCrunch.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "OTP is required")
    private String otp;

    @NotBlank(message = "New Password is required")
    private String newPassword;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;
}