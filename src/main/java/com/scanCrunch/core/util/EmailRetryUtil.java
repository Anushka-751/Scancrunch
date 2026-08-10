package com.scanCrunch.core.util;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Generic retry helper used to retry a "send" style operation a fixed number
 * of times with a short backoff before giving up. Used by the receipt email
 * flow so a transient SMTP failure does not immediately fail the whole
 * request.
 */
@Slf4j
@Component
public class EmailRetryUtil {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final long BACKOFF_MILLIS = 1000L;

    /**
     * Executes {@code action} up to {@code maxAttempts} times. The action
     * should return {@code true} on success and {@code false} (or throw) on
     * failure.
     *
     * @return the number of attempts actually made
     */
    public int executeWithRetry(Supplier<Boolean> action, int maxAttempts) {

        int attempts = 0;
        int effectiveMaxAttempts = maxAttempts > 0 ? maxAttempts : DEFAULT_MAX_ATTEMPTS;

        while (attempts < effectiveMaxAttempts) {

            attempts++;

            try {
                boolean success = Boolean.TRUE.equals(action.get());

                if (success) {
                    return attempts;
                }

                log.warn("Email send attempt {} of {} failed", attempts, effectiveMaxAttempts);

            } catch (Exception ex) {
                log.warn("Email send attempt {} of {} threw an exception: {}",
                        attempts, effectiveMaxAttempts, ex.getMessage());
            }

            if (attempts < effectiveMaxAttempts) {
                sleepQuietly(BACKOFF_MILLIS * attempts);
            }
        }

        return -attempts;
    }

    public int executeWithRetry(Supplier<Boolean> action) {
        return executeWithRetry(action, DEFAULT_MAX_ATTEMPTS);
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
