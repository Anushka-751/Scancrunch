package com.scanCrunch.domain.email.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    void send(String to, String subject, String body);
}