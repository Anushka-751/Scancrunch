package com.scanCrunch.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendResetOtpRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;
}