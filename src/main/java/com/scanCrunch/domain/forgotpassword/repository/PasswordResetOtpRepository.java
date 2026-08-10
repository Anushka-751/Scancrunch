package com.scanCrunch.domain.forgotpassword.repository;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scanCrunch.domain.forgotpassword.entity.PasswordResetOtp;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByIdentifier(String identifier);

    Optional<PasswordResetOtp> findByIdentifierAndOtp(String identifier, String otp);

    Optional<PasswordResetOtp> findByIdentifierAndOtpAndVerified(
            String identifier,
            String otp,
            Boolean verified);

    

@Transactional
void deleteByIdentifier(String identifier);
}