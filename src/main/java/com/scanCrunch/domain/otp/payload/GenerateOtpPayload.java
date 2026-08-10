package com.scanCrunch.domain.otp.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GenerateOtpPayload {

    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String passwordHash;
}