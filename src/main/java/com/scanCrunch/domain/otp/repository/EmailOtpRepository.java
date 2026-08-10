package com.scanCrunch.domain.otp.repository;

import com.scanCrunch.domain.otp.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findByEmail(String email);

    void deleteByEmail(String email);

    boolean existsByEmail(String email);
}