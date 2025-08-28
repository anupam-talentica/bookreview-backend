package com.bookreview.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for JWT token generation and validation
 * 
 * @author BookReview Development Team
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInSeconds;

    @Value("${app.jwt.refresh-expiration}")
    private int refreshExpirationInSeconds;

    /**
     * Generate JWT token for authenticated user
     * 
     * @param authentication the authentication object
     * @return JWT token
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenFromUserId(userPrincipal.getId(), userPrincipal.getEmail(), userPrincipal.getName());
    }

    /**
     * Generate JWT token from user details
     * 
     * @param userId the user ID
     * @param email the user email
     * @param name the user name
     * @return JWT token
     */
    public String generateTokenFromUserId(Long userId, String email, String name) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000L);
        
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("name", name)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generate refresh token
     * 
     * @param userId the user ID
     * @param email the user email
     * @return refresh token
     */
    public String generateRefreshToken(Long userId, String email) {
        Date expiryDate = new Date(System.currentTimeMillis() + refreshExpirationInSeconds * 1000L);
        
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get user email from JWT token
     * 
     * @param token JWT token
     * @return user email
     */
    public String getUserEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Get user ID from JWT token
     * 
     * @param token JWT token
     * @return user ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }

    /**
     * Get user name from JWT token
     * 
     * @param token JWT token
     * @return user name
     */
    public String getUserNameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("name", String.class);
    }

    /**
     * Get token expiration date as LocalDateTime
     * 
     * @param token JWT token
     * @return expiration date
     */
    public LocalDateTime getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Validate JWT token
     * 
     * @param token JWT token to validate
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Check if token is expired
     * 
     * @param token JWT token
     * @return true if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get remaining validity time in seconds
     * 
     * @param token JWT token
     * @return remaining seconds, or 0 if expired/invalid
     */
    public long getRemainingValidityInSeconds(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            long expirationTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();
            
            return Math.max(0, (expirationTime - currentTime) / 1000);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get signing key for JWT
     * 
     * @return SecretKey for signing
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
