package com.scanCrunch.domain.mobileotp.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyMobileOtpPayload {

    private String phoneNumber;
    private String otp;
}