package com.scanCrunch.domain.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationOtpResponse {

    private boolean success;
    private String message;
}