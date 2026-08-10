package com.scanCrunch.core.security;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.scanCrunch.core.exception.RateLimitExceededException;

/**
 * Simple in-memory sliding-window rate limiter used to prevent OTP spam
 * across registration, mobile verification, and forgot-password flows.
 *
 * Keyed by an arbitrary string (e.g. "registration:john@gmail.com" or
 * "mobile:+919999999999") so each OTP type/identifier pair is limited
 * independently.
 *
 * Note: this is process-local state. If the application is ever scaled
 * horizontally across multiple instances, this should be backed by a
 * shared store (e.g. Redis) instead.
 */
@Component
public class OtpRateLimiter {

    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);

    private static final String LIMIT_MESSAGE =
            "Too many OTP requests. Please try again later.";

    private final Map<String, Deque<Instant>> requestLog = new ConcurrentHashMap<>();

    /**
     * Records a new OTP request for the given key and throws
     * {@link RateLimitExceededException} if the caller has exceeded
     * MAX_REQUESTS within the WINDOW.
     */
    public void checkAndRecord(String key) {

        Instant now = Instant.now();

        Deque<Instant> timestamps = requestLog.computeIfAbsent(
                key, k -> new ArrayDeque<>());

        synchronized (timestamps) {

            // Drop timestamps outside the sliding window
            while (!timestamps.isEmpty()
                    && Duration.between(timestamps.peekFirst(), now)
                            .compareTo(WINDOW) > 0) {

                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_REQUESTS) {
                throw new RateLimitExceededException(LIMIT_MESSAGE);
            }

            timestamps.addLast(now);
        }
    }
}
