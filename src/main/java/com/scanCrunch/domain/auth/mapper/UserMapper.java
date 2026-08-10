package com.scanCrunch.domain.auth.mapper;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.auth.dto.AuthResponse;
import com.scanCrunch.domain.auth.dto.RegisterRequest;
import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.user.enums.Role;

@Component
public class UserMapper {

    public User toUser(RegisterRequest request) {

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // Raw password. It will be encrypted in AuthService.
        user.setPassword(request.getPassword());

        // Default role for every registered user
        user.setRole(Role.CUSTOMER);

        user.setActive(true);
        user.setVerified(false);

        return user;
    }

    public User toEntity(RegisterRequest request) {
        return toUser(request);
    }

    public AuthResponse toAuthResponse(User user, String token) {

        AuthResponse response = new AuthResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(token);

        return response;
    }
}