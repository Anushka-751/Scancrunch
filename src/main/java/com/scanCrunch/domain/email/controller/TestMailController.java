package com.scanCrunch.domain.email.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestMailController {

    private final JavaMailSender mailSender;

    @GetMapping("/email")
    public String sendTestMail() {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("anushkapujari15@gmail.com"); // Replace with your email
        message.setSubject("Spring Boot Email Test");
        message.setText("Hello! This is a test email from ScanCrunch");

        mailSender.send(message);

        return "Email sent successfully";
    }
}
