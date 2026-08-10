package com.scanCrunch.domain.mobileotp.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobileOtpDto {

    private Long id;
    private String phoneNumber;
    private String otp;
    private LocalDateTime expiresAt;
    private Boolean verified;
}