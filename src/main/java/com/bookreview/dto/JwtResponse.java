package com.bookreview.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for JWT authentication response
 * 
 * @author BookReview Development Team
 */
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String email;
    private LocalDateTime expiresAt;

    // Constructors
    public JwtResponse() {
    }

    public JwtResponse(String token, Long userId, String name, String email, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "type='" + type + '\'' +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
