package com.scanCrunch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * Application-level mail settings that sit alongside the auto-configured
 * JavaMailSender built by spring-boot-starter-mail from the
 * spring.mail.* properties in application-*.yml.
 */
@Getter
@Configuration
public class MailConfig {

    @Value("${app.restaurant.name:ScanCrunch}")
    private String restaurantName;

    @Value("${spring.mail.username:}")
    private String fromAddress;
}