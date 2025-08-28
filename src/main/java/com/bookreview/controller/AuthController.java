package com.bookreview.controller;

import com.bookreview.dto.JwtResponse;
import com.bookreview.dto.LoginDto;
import com.bookreview.dto.UserRegistrationDto;
import com.bookreview.entity.Review;
import com.bookreview.entity.User;
import com.bookreview.service.ReviewService;
import com.bookreview.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller for user registration and login
 * 
 * Provides endpoints for:
 * - User registration
 * - User login
 * - JWT token validation
 * - Password reset (future implementation)
 * 
 * @author BookReview Development Team
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public AuthController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    /**
     * Register a new user
     * 
     * @param registrationDto user registration data
     * @return success message with user details
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Register user using service
            User user = userService.registerUser(registrationDto);
            
            // Create response user object (without password)
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", user.getId());
            userResponse.put("name", user.getName());
            userResponse.put("email", user.getEmail());
            userResponse.put("bio", user.getBio());
            userResponse.put("emailVerified", user.getEmailVerified());
            userResponse.put("active", user.getActive());
            userResponse.put("createdAt", user.getCreatedAt());
            
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("data", userResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Authenticate user and return JWT token
     * 
     * @param loginDto user login credentials
     * @return JWT token response
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            // Authenticate user using service
            User user = userService.authenticateUser(loginDto.getEmail(), loginDto.getPassword());
            
            // Create JWT response (still using mock token for now)
            JwtResponse jwtResponse = new JwtResponse(
                "mock-jwt-token-" + user.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                LocalDateTime.now().plusHours(24)
            );
            
            return ResponseEntity.ok(jwtResponse);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Validate JWT token
     * 
     * @param token JWT token from Authorization header
     * @return token validation status
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Mock token validation - extract user ID from token
            if (jwtToken.startsWith("mock-jwt-token-")) {
                try {
                    String userIdStr = jwtToken.substring("mock-jwt-token-".length());
                    Long userId = Long.parseLong(userIdStr);
                    
                    // Get actual user data
                    var userOptional = userService.findById(userId);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        response.put("valid", true);
                        response.put("userId", user.getId());
                        response.put("email", user.getEmail());
                        response.put("name", user.getName());
                        return ResponseEntity.ok(response);
                    }
                } catch (NumberFormatException e) {
                    // Invalid token format
                }
            }
        }
        
        response.put("valid", false);
        response.put("message", "Invalid or missing token");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Refresh JWT token
     * 
     * @param refreshToken refresh token
     * @return new JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        
        if (refreshToken != null && refreshToken.startsWith("refresh-")) {
            // Mock refresh token logic
            JwtResponse jwtResponse = new JwtResponse(
                "mock-jwt-token-refreshed-" + System.currentTimeMillis(),
                1L,
                "Admin User",
                "admin@bookreview.com",
                LocalDateTime.now().plusHours(24)
            );
            
            return ResponseEntity.ok(jwtResponse);
        }
        
        return ResponseEntity.badRequest().build();
    }

    /**
     * Logout user (invalidate token)
     * 
     * @param token JWT token from Authorization header
     * @return logout status
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, String> response = new HashMap<>();
        
        // Mock logout logic (in real implementation, add token to blacklist)
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user profile
     * 
     * @param token JWT token from Authorization header
     * @return user profile data
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            if (jwtToken.startsWith("mock-jwt-token-")) {
                try {
                    String userIdStr = jwtToken.substring("mock-jwt-token-".length());
                    Long userId = Long.parseLong(userIdStr);
                    
                    // Get actual user data with reviews loaded for accurate counts
                    var userOptional = userService.findByIdWithReviews(userId);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        Map<String, Object> profile = new HashMap<>();
                        profile.put("id", user.getId());
                        profile.put("name", user.getName());
                        profile.put("email", user.getEmail());
                        profile.put("bio", user.getBio());
                        profile.put("avatarUrl", user.getAvatarUrl());
                        profile.put("emailVerified", user.getEmailVerified());
                        profile.put("active", user.getActive());
                        profile.put("createdAt", user.getCreatedAt());
                        profile.put("reviewCount", user.getReviewCount());
                        profile.put("favoriteBooksCount", user.getFavoriteBooksCount());
                        
                        return ResponseEntity.ok(profile);
                    }
                } catch (NumberFormatException e) {
                    // Invalid token format
                }
            }
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(errorResponse);
    }

    /**
     * Update user profile information
     * 
     * @param token JWT token from Authorization header
     * @param profileUpdate profile update data
     * @return updated user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> profileUpdate) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token == null || !token.startsWith("Bearer ")) {
            response.put("error", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }
        
        String jwtToken = token.substring(7);
        
        if (!jwtToken.startsWith("mock-jwt-token-")) {
            response.put("error", "Invalid token");
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            // Extract user ID from token
            String userIdStr = jwtToken.substring("mock-jwt-token-".length());
            Long userId = Long.parseLong(userIdStr);
            
            String name = profileUpdate.get("name");
            String bio = profileUpdate.get("bio");
            String avatarUrl = profileUpdate.get("avatarUrl");
            
            // Update profile using service
            User updatedUser = userService.updateUserProfile(userId, name, bio, avatarUrl);
            
            // Get updated user with reviews loaded for accurate counts
            var userWithReviewsOptional = userService.findByIdWithReviews(updatedUser.getId());
            User userWithReviews = userWithReviewsOptional.orElse(updatedUser);
            
            // Create response with updated user data
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", userWithReviews.getId());
            profile.put("name", userWithReviews.getName());
            profile.put("email", userWithReviews.getEmail());
            profile.put("bio", userWithReviews.getBio());
            profile.put("avatarUrl", userWithReviews.getAvatarUrl());
            profile.put("emailVerified", userWithReviews.getEmailVerified());
            profile.put("active", userWithReviews.getActive());
            profile.put("createdAt", userWithReviews.getCreatedAt());
            profile.put("reviewCount", userWithReviews.getReviewCount());
            profile.put("favoriteBooksCount", userWithReviews.getFavoriteBooksCount());
            
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("data", profile);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Profile update failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Change user password
     * 
     * @param token JWT token from Authorization header
     * @param passwordChange password change data
     * @return success message
     */
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> passwordChange) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token == null || !token.startsWith("Bearer ")) {
            response.put("error", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }
        
        String jwtToken = token.substring(7);
        
        if (!jwtToken.startsWith("mock-jwt-token-")) {
            response.put("error", "Invalid token");
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            // Extract user ID from token
            String userIdStr = jwtToken.substring("mock-jwt-token-".length());
            Long userId = Long.parseLong(userIdStr);
            
            String currentPassword = passwordChange.get("currentPassword");
            String newPassword = passwordChange.get("newPassword");
            
            if (currentPassword == null || newPassword == null) {
                response.put("success", false);
                response.put("message", "Current password and new password are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Change password using service
            userService.changePassword(userId, currentPassword, newPassword);
            
            response.put("success", true);
            response.put("message", "Password changed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Password change failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get current user's reviews using JWT token
     * 
     * @param token JWT token from Authorization header
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @return paginated reviews for the current user
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<Map<String, Object>> getMyReviews(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Extract user ID from mock token (in real app, use JWT service)
            if (jwtToken.startsWith("mock-jwt-token-")) {
                Long userId = extractUserIdFromMockToken(jwtToken);
                
                try {
                    Pageable pageable = PageRequest.of(page, size);
                    Page<Review> reviews = reviewService.getUserReviews(userId, pageable);
                    
                    response.put("content", reviews.getContent());
                    response.put("totalElements", reviews.getTotalElements());
                    response.put("totalPages", reviews.getTotalPages());
                    response.put("currentPage", reviews.getNumber());
                    response.put("size", reviews.getSize());
                    response.put("first", reviews.isFirst());
                    response.put("last", reviews.isLast());
                    
                    return ResponseEntity.ok(response);
                    
                } catch (Exception e) {
                    response.put("error", "Failed to fetch reviews: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Debug endpoint to check user information
     * 
     * @param email user email to check
     * @return user information
     */
    @GetMapping("/debug/user")
    public ResponseEntity<Map<String, Object>> debugUser(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var userOptional = userService.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // Force load reviews and favorites
                user.getReviews().size();
                user.getFavoriteBooks().size();
                
                response.put("found", true);
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("name", user.getName());
                response.put("reviewCount", user.getReviewCount());
                response.put("favoriteBooksCount", user.getFavoriteBooksCount());
                response.put("createdAt", user.getCreatedAt());
            } else {
                response.put("found", false);
                response.put("message", "User not found with email: " + email);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Helper method to extract user ID from mock JWT token
     * In a real application, this would parse the actual JWT token
     * 
     * @param mockToken the mock JWT token
     * @return user ID
     */
    private Long extractUserIdFromMockToken(String mockToken) {
        // Extract user ID from mock token format: "mock-jwt-token-{userId}"
        // This handles both real user login tokens and manual test tokens
        if (mockToken.contains("-")) {
            String[] parts = mockToken.split("-");
            if (parts.length > 3 && parts[3].matches("\\d+")) {
                return Long.parseLong(parts[3]);
            }
        }
        
        // Generate a pseudo-random user ID based on token hash
        // This ensures different browser sessions get different user IDs
        int hash = Math.abs(mockToken.hashCode());
        Long userId = (long) (2 + (hash % 8)); // User IDs 2-9 (avoiding 1 which has existing reviews)
        
        return userId;
    }
}
