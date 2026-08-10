package com.scanCrunch.core.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import com.scanCrunch.core.exception.InvalidJwtException;
import com.scanCrunch.core.exception.JwtExpiredException;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Used by AuthServiceImpl and OAuth2SuccessHandler
    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Existing filter expects this name
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Existing filter expects this method
    public boolean isTokenValid(
            String token,
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    private Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Validates signature, format, and expiration of a raw JWT string.
     * Throws {@link JwtExpiredException} for an expired-but-well-formed
     * token, and {@link InvalidJwtException} for anything else that is
     * malformed, tampered with, or otherwise invalid (bad signature,
     * unsupported format, blank/null input).
     *
     * Used by the validate-token API and can be reused by the
     * authentication filter for consistent error reporting.
     */
    public Claims validateTokenOrThrow(String token) {

        if (token == null || token.isBlank()) {
            throw new InvalidJwtException("Token must not be empty.");
        }

        try {

            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {

            throw new JwtExpiredException("JWT token has expired.", e);

        } catch (JwtException | IllegalArgumentException e) {

            // Covers malformed tokens, bad signatures, and tampered
            // payloads (jjwt rejects any signature mismatch here).
            throw new InvalidJwtException("Invalid or tampered JWT token.", e);
        }
    }

    /**
     * Returns true/false instead of throwing - convenience for callers
     * that only need a boolean (e.g. the /validate-token API).
     */
    public boolean isTokenStructurallyValid(String token) {

        try {
            validateTokenOrThrow(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
