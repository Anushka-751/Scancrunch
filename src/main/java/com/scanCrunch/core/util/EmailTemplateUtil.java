package com.scanCrunch.core.util;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateUtil {

    public String registrationOtpTemplate(
            String firstName,
            String otp) {

        return """
                Hello %s,

                Welcome to ScanCrunch!

                Your email verification OTP is:

                %s

                This OTP is valid for 5 minutes.

                Please do not share this OTP with anyone.

                If you did not request this registration, you can safely ignore this email.

                Regards,
                ScanCrunch Team
                """.formatted(firstName, otp);
    }
}