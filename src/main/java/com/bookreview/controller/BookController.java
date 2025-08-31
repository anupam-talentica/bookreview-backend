package com.bookreview.controller;

import com.bookreview.entity.Book;
import com.bookreview.entity.Review;
import com.bookreview.dto.BookDetailsDto;
import com.bookreview.dto.ReviewDto;

import com.bookreview.service.BookService;
import com.bookreview.service.ReviewService;
import com.bookreview.service.OpenAIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Book Controller for managing books and book-related operations
 * 
 * Provides endpoints for:
 * - Get all books with pagination
 * - Get book by ID
 * - Search books
 * - Get popular/top-rated books
 * - Add book to favorites
 * - Get book reviews
 * 
 * @author BookReview Development Team
 */
@RestController
@RequestMapping("/books")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://bookreview-frontend.s3-website.ap-south-1.amazonaws.com", "http://bookreview-frontend-staging.s3-website.ap-south-1.amazonaws.com"}, allowedHeaders = "*", allowCredentials = "true")
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;
    private final OpenAIService openAIService;

    @Autowired
    public BookController(BookService bookService, ReviewService reviewService, OpenAIService openAIService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
        this.openAIService = openAIService;
    }

    /**
     * Get all books with pagination
     * 
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param sort sort field (default: createdAt)
     * @param direction sort direction (default: desc)
     * @return paginated list of books
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        // Create sort direction
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        // Create pageable with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        // Get books from database
        Page<Book> bookPage = bookService.getAllBooks(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", bookPage.getContent());
        response.put("totalElements", bookPage.getTotalElements());
        response.put("totalPages", bookPage.getTotalPages());
        response.put("currentPage", bookPage.getNumber());
        response.put("size", bookPage.getSize());
        response.put("first", bookPage.isFirst());
        response.put("last", bookPage.isLast());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get book by ID
     * 
     * @param id book ID
     * @return book details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookById(@PathVariable Long id) {
        Optional<BookDetailsDto> bookDetailsDtoOptional = bookService.getBookById(id);
        
        if (bookDetailsDtoOptional.isPresent()) {
            BookDetailsDto bookDetailsDto = bookDetailsDtoOptional.get();
            
            // Create response with book details and additional info
            Map<String, Object> response = new HashMap<>();
            response.put("id", bookDetailsDto.getId());
            response.put("title", bookDetailsDto.getTitle());
            response.put("author", bookDetailsDto.getAuthor());
            response.put("description", bookDetailsDto.getDescription());
            response.put("coverImageUrl", bookDetailsDto.getCoverImageUrl());
            response.put("genres", bookDetailsDto.getGenres());
            response.put("publishedYear", bookDetailsDto.getPublishedYear());
            response.put("averageRating", bookDetailsDto.getAverageRating());
            response.put("reviewCount", bookDetailsDto.getReviewCount());
            response.put("reviews", bookDetailsDto.getReviews());
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Search books by title, author, or genre
     * 
     * @param query search query
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @return search results
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> searchResults = bookService.searchBooks(query, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", searchResults.getContent());
        response.put("totalElements", searchResults.getTotalElements());
        response.put("totalPages", searchResults.getTotalPages());
        response.put("currentPage", searchResults.getNumber());
        response.put("size", searchResults.getSize());
        response.put("query", query);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get top-rated books
     * 
     * @param limit number of books to return (default: 10)
     * @return list of top-rated books
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Book>> getTopRatedBooks(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        Page<Book> topRatedBooks = bookService.getTopRatedBooks(new BigDecimal("3.0"), pageable);
        
        return ResponseEntity.ok(topRatedBooks.getContent());
    }

    /**
     * Get popular books (most reviewed)
     * 
     * @param limit number of books to return (default: 10)
     * @return list of popular books
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Book>> getPopularBooks(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        Page<Book> popularBooks = bookService.getPopularBooks(1, pageable);
        
        return ResponseEntity.ok(popularBooks.getContent());
    }

    /**
     * Get recently added books
     * 
     * @param limit number of books to return (default: 10)
     * @return list of recently added books
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Book>> getRecentBooks(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        Page<Book> recentBooks = bookService.getRecentBooks(pageable);
        
        return ResponseEntity.ok(recentBooks.getContent());
    }

    /**
     * Add book to user's favorites
     * 
     * @param id book ID
     * @param token JWT token from Authorization header
     * @return success/failure status
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> addToFavorites(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Extract user ID from mock token (in real app, use JWT service)
            if (jwtToken.startsWith("mock-jwt-token-")) {
                Long userId = extractUserIdFromMockToken(jwtToken);
                
                try {
                    boolean added = bookService.addToFavorites(id, userId);
                    if (added) {
                        response.put("success", true);
                        response.put("message", "Book added to favorites");
                        response.put("bookId", id);
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("success", false);
                        response.put("message", "Book is already in favorites");
                        return ResponseEntity.ok(response);
                    }
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Failed to add to favorites: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Remove book from user's favorites
     * 
     * @param id book ID
     * @param token JWT token from Authorization header
     * @return success/failure status
     */
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> removeFromFavorites(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Extract user ID from mock token (in real app, use JWT service)
            if (jwtToken.startsWith("mock-jwt-token-")) {
                Long userId = extractUserIdFromMockToken(jwtToken);
                
                try {
                    boolean removed = bookService.removeFromFavorites(id, userId);
                    if (removed) {
                        response.put("success", true);
                        response.put("message", "Book removed from favorites");
                        response.put("bookId", id);
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("success", false);
                        response.put("message", "Book was not in favorites");
                        return ResponseEntity.ok(response);
                    }
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Failed to remove from favorites: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Check if book is in user's favorites
     * 
     * @param id book ID
     * @param token JWT token from Authorization header
     * @return favorite status
     */
    @GetMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> isFavorited(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Extract user ID from mock token (in real app, use JWT service)
            if (jwtToken.startsWith("mock-jwt-token-")) {
                Long userId = extractUserIdFromMockToken(jwtToken);
                
                boolean favorited = bookService.isBookFavorited(id, userId);
                response.put("favorited", favorited);
                response.put("bookId", id);
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("favorited", false);
        response.put("bookId", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's favorite books
     * 
     * @param token JWT token from Authorization header
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @return paginated favorite books
     */
    @GetMapping("/favorites")
    public ResponseEntity<Map<String, Object>> getUserFavorites(
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
                    Page<Book> favoritesPage = bookService.getUserFavorites(userId, pageable);
                    
                    response.put("content", favoritesPage.getContent());
                    response.put("totalElements", favoritesPage.getTotalElements());
                    response.put("totalPages", favoritesPage.getTotalPages());
                    response.put("currentPage", favoritesPage.getNumber());
                    response.put("size", favoritesPage.getSize());
                    response.put("first", favoritesPage.isFirst());
                    response.put("last", favoritesPage.isLast());
                    
                    return ResponseEntity.ok(response);
                } catch (Exception e) {
                    response.put("error", "Failed to fetch favorites: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Get reviews for a book
     * 
     * @param id book ID
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @return paginated reviews
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<Map<String, Object>> getBookReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDto> reviews = reviewService.getBookReviewDtos(id, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", reviews.getContent());
        response.put("totalElements", reviews.getTotalElements());
        response.put("totalPages", reviews.getTotalPages());
        response.put("currentPage", reviews.getNumber());
        response.put("size", reviews.getSize());
        response.put("bookId", id);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create a review for a book
     * 
     * @param id book ID
     * @param reviewData review data
     * @param token JWT token from Authorization header
     * @return created review
     */
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Map<String, Object>> createReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> reviewData,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Extract user ID from mock token (in real app, use JWT service)
            if (jwtToken.startsWith("mock-jwt-token-")) {
                Long userId = extractUserIdFromMockToken(jwtToken);
                
                try {
                    Integer rating = (Integer) reviewData.get("rating");
                    String reviewText = (String) reviewData.get("reviewText");
                    
                    Review review = reviewService.createReview(id, userId, rating, reviewText);
                    
                    response.put("success", true);
                    response.put("message", "Review created successfully");
                    response.put("review", review);
                    return ResponseEntity.ok(response);
                    
                } catch (IllegalArgumentException e) {
                    response.put("success", false);
                    response.put("message", e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Failed to create review: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Get personalized recommendations for a user
     * 
     * @param token JWT token from Authorization header
     * @param limit number of recommendations to return (default: 10)
     * @return list of recommended books with explanations
     */
    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Extract user ID from mock token (in real app, use JWT service)
            if (jwtToken.startsWith("mock-jwt-token-")) {
                Long userId = extractUserIdFromMockToken(jwtToken);
                
                try {
                    Map<String, Object> recommendations = bookService.getPersonalizedRecommendations(userId, limit);
                    return ResponseEntity.ok(recommendations);
                } catch (Exception e) {
                    response.put("error", "Failed to fetch recommendations: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Get similar books to a specific book
     * 
     * @param id book ID to find similar books for
     * @param limit number of similar books to return (default: 10)
     * @return list of similar books
     */
    @GetMapping("/{id}/similar")
    public ResponseEntity<Map<String, Object>> getSimilarBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Book> similarBooks = bookService.getSimilarBooks(id, pageable);
            
            response.put("bookId", id);
            response.put("similarBooks", similarBooks);
            response.put("count", similarBooks.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to fetch similar books: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Submit feedback on a recommendation
     * 
     * @param recommendationId recommendation ID (for now, just book ID)
     * @param token JWT token from Authorization header
     * @param feedback feedback data (like/dislike, reason)
     * @return success/failure status
     */
    @PostMapping("/recommendations/{recommendationId}/feedback")
    public ResponseEntity<Map<String, Object>> submitRecommendationFeedback(
            @PathVariable Long recommendationId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> feedback) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Extract user ID from mock token (in real app, use JWT service)
            if (jwtToken.startsWith("mock-jwt-token-")) {
                Long userId = extractUserIdFromMockToken(jwtToken);
                
                try {
                    // For now, just log the feedback (in real app, store in database)
                    String feedbackType = (String) feedback.get("type"); // "like" or "dislike"
                    String reason = (String) feedback.get("reason");
                    
                    // Log feedback for now (TODO: Store in database for learning)
                    System.out.println("Recommendation feedback - User: " + userId + 
                                     ", Book: " + recommendationId + 
                                     ", Type: " + feedbackType + 
                                     (reason != null ? ", Reason: " + reason : ""));
                    
                    response.put("success", true);
                    response.put("message", "Feedback received");
                    response.put("recommendationId", recommendationId);
                    response.put("feedbackType", feedbackType);
                    response.put("userId", userId);
                    
                    return ResponseEntity.ok(response);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Failed to submit feedback: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        
        response.put("error", "Unauthorized");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Get AI-powered recommendations for a user based on their favorite books
     * 
     * @param token JWT token from Authorization header
     * @param limit number of recommendations to return (default: 3)
     * @return AI-generated book recommendations
     */
    @GetMapping("/ai-recommendations")
    public ResponseEntity<Map<String, Object>> getAIRecommendations(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "3") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Check if OpenAI service is available
        if (!openAIService.isAvailable()) {
            response.put("success", false);
            response.put("message", "AI recommendations are not available. Please contact administrator to configure OpenAI.");
            response.put("aiAvailable", false);
            return ResponseEntity.ok(response);
        }
        
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            // Extract user ID from mock token (in real app, use JWT service)
            if (jwtToken.startsWith("mock-jwt-token-")) {
                Long userId = extractUserIdFromMockToken(jwtToken);
                
                try {
                    Map<String, Object> aiRecommendations = bookService.getAIRecommendations(userId, limit);
                    aiRecommendations.put("aiAvailable", true);
                    return ResponseEntity.ok(aiRecommendations);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Failed to fetch AI recommendations: " + e.getMessage());
                    response.put("aiAvailable", true);
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        
        response.put("error", "Unauthorized");
        response.put("aiAvailable", true);
        return ResponseEntity.status(401).body(response);
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
