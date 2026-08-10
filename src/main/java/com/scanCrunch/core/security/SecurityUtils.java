package com.scanCrunch.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.scanCrunch.domain.user.entity.User;

@Component
public class SecurityUtils {

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            return null;
        }

        return customUserDetails.getUser();
    }

    public Long getCurrentUserId() {

        User user = getCurrentUser();

        return user != null ? user.getId() : null;
    }
}