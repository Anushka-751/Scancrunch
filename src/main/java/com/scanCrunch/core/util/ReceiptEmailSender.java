package com.scanCrunch.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends the HTML payment receipt email. Kept deliberately thin: building the
 * HTML body is the responsibility of {@link HtmlReceiptGenerator} /
 * {@link ReceiptTemplateBuilder}; this class only knows how to hand a
 * finished HTML string to the SMTP layer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEmailSender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${app.restaurant.name:ScanCrunch}")
    private String restaurantName;

    /**
     * Sends an HTML email. Returns {@code true} if the message was handed
     * off to the mail sender successfully, {@code false} otherwise. Never
     * throws so callers (retry logic) can treat this as a simple
     * success/failure signal.
     */
    public boolean send(String to, String subject, String htmlBody) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (fromAddress != null && !fromAddress.isBlank()) {
                helper.setFrom(fromAddress, restaurantName);
            }

            mailSender.send(message);

            return true;

        } catch (Exception e) {
            log.warn("Failed to send receipt email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
