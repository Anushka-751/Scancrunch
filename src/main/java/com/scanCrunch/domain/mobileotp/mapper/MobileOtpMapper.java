package com.scanCrunch.domain.mobileotp.mapper;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.mobileotp.dto.MobileOtpDto;
import com.scanCrunch.domain.mobileotp.entity.MobileOtp;

@Component
public class MobileOtpMapper {

    public MobileOtpDto toDto(MobileOtp entity) {

        if (entity == null) {
            return null;
        }

        MobileOtpDto dto = new MobileOtpDto();

        dto.setId(entity.getId());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setOtp(entity.getOtp());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setVerified(entity.getVerified());

        return dto;
    }

    public MobileOtp toEntity(MobileOtpDto dto) {

        if (dto == null) {
            return null;
        }

        MobileOtp entity = new MobileOtp();

        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setOtp(dto.getOtp());
        entity.setExpiresAt(dto.getExpiresAt());
        entity.setVerified(dto.getVerified());

        return entity;
    }
}