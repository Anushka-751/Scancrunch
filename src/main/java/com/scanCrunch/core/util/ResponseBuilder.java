package com.scanCrunch.core.util;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds the standardized error envelope used across the auth/security
 * module:
 *
 * {
 *   "success": false,
 *   "message": "Error description",
 *   "timestamp": "2026-07-28T12:30:45"
 * }
 *
 * Kept separate from the existing domain-specific ApiResponse<T> DTOs
 * (which several other modules already depend on for their "data"
 * field) so this can be adopted incrementally without touching those.
 */
public final class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static Map<String, Object> error(String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());

        return body;
    }

    public static Map<String, Object> success(String message, Object data) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());

        if (data != null) {
            body.put("data", data);
        }

        return body;
    }
}
