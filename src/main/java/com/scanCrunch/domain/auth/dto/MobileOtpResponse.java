package com.scanCrunch.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MobileOtpResponse {

    private boolean success;
    private String message;
}