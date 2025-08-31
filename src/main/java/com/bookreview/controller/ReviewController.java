package com.bookreview.controller;

import com.bookreview.entity.Review;
import com.bookreview.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Review Controller for managing review-specific operations
 * 
 * Provides endpoints for:
 * - Update existing reviews
 * - Delete reviews
 * 
 * @author BookReview Development Team
 */
@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://bookreview-frontend.s3-website.ap-south-1.amazonaws.com", "http://bookreview-frontend-staging.s3-website.ap-south-1.amazonaws.com"}, allowedHeaders = "*", allowCredentials = "true")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Update an existing review
     * 
     * @param reviewId review ID
     * @param reviewData review data (rating, reviewText)
     * @return updated review
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody Map<String, Object> reviewData) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Get authenticated user from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Long userId = (Long) authentication.getPrincipal();
            
            try {
                Integer rating = (Integer) reviewData.get("rating");
                String reviewText = (String) reviewData.get("reviewText");
                
                Review review = reviewService.updateReview(reviewId, userId, rating, reviewText);
                
                response.put("success", true);
                response.put("message", "Review updated successfully");
                response.put("review", review);
                return ResponseEntity.ok(response);
                
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Failed to update review: " + e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Delete a review
     * 
     * @param reviewId review ID
     * @return success/failure status
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long reviewId) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Get authenticated user from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Long userId = (Long) authentication.getPrincipal();
            
            try {
                reviewService.deleteReview(reviewId, userId);
                
                response.put("success", true);
                response.put("message", "Review deleted successfully");
                response.put("reviewId", reviewId);
                return ResponseEntity.ok(response);
                
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Failed to delete review: " + e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }
}
