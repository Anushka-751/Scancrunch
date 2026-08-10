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
public class SendPasswordOtpRequest {

    @NotBlank(message = "OTP type (EMAIL or MOBILE) is required")
    private String type;
}
