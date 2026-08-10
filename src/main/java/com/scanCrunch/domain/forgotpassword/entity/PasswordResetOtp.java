package com.scanCrunch.domain.forgotpassword.entity;

import java.time.LocalDateTime;

import com.scanCrunch.core.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "password_reset_otp")
public class PasswordResetOtp extends BaseEntity {

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(name = "resend_count")
    private Integer resendCount = 0;

    @Column(name = "last_sent_at")
    private LocalDateTime lastSentAt;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;
}