package com.scanCrunch.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendMobileOtpRequest {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}