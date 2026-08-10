package com.scanCrunch.core.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanCrunch.core.util.ResponseBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Replaces Spring Security's default 401 handling with the module's
 * standardized JSON error envelope. If JwtAuthenticationFilter caught a
 * specific JWT problem (expired / malformed / tampered), that message
 * is surfaced here via the "jwt_exception_message" request attribute;
 * otherwise a generic "Unauthorized" message is returned.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        Object attribute = request.getAttribute("jwt_exception_message");

        String message = attribute != null
                ? attribute.toString()
                : "Authentication required.";

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ResponseBuilder.error(message)));
    }
}
