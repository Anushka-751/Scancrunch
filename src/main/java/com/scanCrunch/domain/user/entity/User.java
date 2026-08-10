package com.scanCrunch.domain.user.entity;

import com.scanCrunch.core.entity.BaseEntity;
import com.scanCrunch.domain.user.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    // Google users do not have phone
    @Column(name = "phone", nullable = true)
    private String phone;

    // Google users do not have password
    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(name = "verified_email", nullable = false)
    private Boolean verifiedEmail = false;

    @Column(name = "verified_phone", nullable = false)
    private Boolean verifiedPhone = false;

    @Column(name = "phone_verified", nullable = false)
    private Boolean phoneVerified = false;

    // EMAIL / GOOGLE
    @Column(name = "provider")
    private String provider;
}
