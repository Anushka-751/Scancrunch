package com.scanCrunch.domain.forgotpassword.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOtpRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

}