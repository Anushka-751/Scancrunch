package com.scanCrunch.core.util;

import java.time.LocalDateTime;

public class PasswordResetUtil {

    private PasswordResetUtil() {
    }

    public static LocalDateTime getExpiryTime() {
        return LocalDateTime.now().plusMinutes(5);
    }

    public static boolean isExpired(LocalDateTime expiryTime) {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}