package com.scanCrunch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Dedicated home for the password-encoding bean, split out of
 * SecurityConfig per the auth/security module's structure. All
 * password hashing/verification across the app (registration, login,
 * password reset) goes through this single BCryptPasswordEncoder bean.
 */
@Configuration
public class BCryptConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
