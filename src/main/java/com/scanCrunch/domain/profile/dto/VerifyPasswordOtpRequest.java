package com.scanCrunch.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPasswordOtpRequest {

    @NotBlank(message = "OTP is required")
    private String otp;
}
