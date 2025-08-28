package com.bookreview.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * JWT Utility class for token generation, validation, and parsing
 * 
 * @author BookReview Development Team
 */
@Component
public class JwtUtils {

    @Value("${app.jwt.secret:dev-jwt-secret-key-not-secure}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 hours
    private int jwtExpirationMs;

    /**
     * Generate JWT token for user
     */
    public String generateJwtToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get signing key from secret
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Get user ID from JWT token
     */
    public Long getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Get email from JWT token
     */
    public String getEmailFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("email");
    }

    /**
     * Validate JWT token
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Handle mock tokens for development
            if (authToken.startsWith("mock-jwt-token-")) {
                return true; // Accept mock tokens for development
            }
            
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    /**
     * Extract user ID from token (handles both real JWT and mock tokens)
     */
    public Long extractUserIdFromToken(String token) {
        if (token.startsWith("mock-jwt-token-")) {
            // Handle mock tokens
            return extractUserIdFromMockToken(token);
        } else {
            // Handle real JWT tokens
            return getUserIdFromJwtToken(token);
        }
    }

    /**
     * Extract user ID from mock token for development
     */
    private Long extractUserIdFromMockToken(String mockToken) {
        if (mockToken.contains("-")) {
            String[] parts = mockToken.split("-");
            if (parts.length > 3 && parts[3].matches("\\d+")) {
                return Long.parseLong(parts[3]);
            }
        }
        
        // Generate a pseudo-random user ID based on token hash
        int hash = Math.abs(mockToken.hashCode());
        return (long) (2 + (hash % 8)); // User IDs 2-9
    }
}
