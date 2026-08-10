package com.scanCrunch.domain.email.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.scanCrunch.domain.email.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            System.out.println("========== EMAIL SENT SUCCESSFULLY ==========");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("============================================");
        } catch (Exception e) {
            System.err.println("========== EMAIL SENDING FAILED (SMTP REFUSED) ==========");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Fallback - Printing Email Details to Console:");
            System.err.println("To: " + to);
            System.err.println("Subject: " + subject);
            System.err.println("Body:\n" + body);
            System.err.println("========================================================");
        }
    }

    @Override
    public void send(String to, String subject, String body) {
        sendEmail(to, subject, body);
    }
}