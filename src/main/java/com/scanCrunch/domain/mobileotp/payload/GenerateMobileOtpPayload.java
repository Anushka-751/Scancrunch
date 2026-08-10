package com.scanCrunch.domain.mobileotp.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateMobileOtpPayload {

    private String phoneNumber;
}