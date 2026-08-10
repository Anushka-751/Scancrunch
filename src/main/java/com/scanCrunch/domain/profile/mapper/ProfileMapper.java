package com.scanCrunch.domain.profile.mapper;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.profile.dto.ProfileResponse;
import com.scanCrunch.domain.user.entity.User;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return ProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .verifiedEmail(user.getVerifiedEmail() != null ? user.getVerifiedEmail() : Boolean.FALSE)
                .verifiedPhone(user.getVerifiedPhone() != null ? user.getVerifiedPhone() : Boolean.FALSE)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
