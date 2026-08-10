package com.scanCrunch.domain.mobileotp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scanCrunch.domain.mobileotp.entity.MobileOtp;

public interface MobileOtpRepository extends JpaRepository<MobileOtp, Long> {

    Optional<MobileOtp> findByPhoneNumber(String phoneNumber);

    Optional<MobileOtp> findByPhoneNumberAndVerifiedTrue(String phoneNumber);

    void deleteByPhoneNumber(String phoneNumber);
}