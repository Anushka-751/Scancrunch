package com.scanCrunch.core.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSenderUtil {

    private final JavaMailSender mailSender;
    private final EmailTemplateUtil emailTemplateUtil;

    public void sendRegistrationOtp(
            String email,
            String firstName,
            String otp) {

        String emailBody = emailTemplateUtil.registrationOtpTemplate(
                firstName,
                otp);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("ScanCrunch - Email Verification OTP");
        message.setText(emailBody);

        try {
            mailSender.send(message);
            System.out.println("========== REGISTRATION EMAIL SENT SUCCESSFULLY ==========");
            System.out.println("To: " + email);
            System.out.println("=========================================================");
        } catch (Exception e) {
            System.err.println("========== REGISTRATION EMAIL SENDING FAILED (SMTP REFUSED) ==========");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Fallback - Printing Email Details to Console:");
            System.err.println("To: " + email);
            System.err.println("Subject: ScanCrunch - Email Verification OTP");
            System.err.println("Body:\n" + emailBody);
            System.err.println("=======================================================================");
        }
    }
}