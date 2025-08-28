package com.bookreview.service;

import com.bookreview.dto.ReviewDto;
import com.bookreview.entity.Book;
import com.bookreview.entity.Review;
import com.bookreview.entity.User;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.ReviewRepository;
import com.bookreview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Review management operations
 * 
 * Handles:
 * - Review creation and management
 * - Review retrieval with pagination
 * - Rating calculations
 * - Review validation
 * 
 * @author BookReview Development Team
 */
@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get reviews for a specific book
     * 
     * @param bookId book ID
     * @param pageable pagination information
     * @return page of reviews
     */
    @Transactional(readOnly = true)
    public Page<Review> getBookReviews(Long bookId, Pageable pageable) {
        return reviewRepository.findByBookIdWithUser(bookId, pageable);
    }

    /**
     * Get reviews for a specific book as DTOs
     * 
     * @param bookId book ID
     * @param pageable pagination information
     * @return page of review DTOs
     */
    @Transactional(readOnly = true)
    public Page<ReviewDto> getBookReviewDtos(Long bookId, Pageable pageable) {
        Page<Review> reviewsPage = reviewRepository.findByBookIdWithUser(bookId, pageable);
        
        List<ReviewDto> reviewDtos = reviewsPage.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        return new PageImpl<>(reviewDtos, pageable, reviewsPage.getTotalElements());
    }

    /**
     * Convert Review entity to ReviewDto
     * 
     * @param review the review entity
     * @return review DTO
     */
    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        
        // Create nested user object
        ReviewDto.UserDto userDto = new ReviewDto.UserDto(
            review.getUser().getId(),
            review.getUser().getName(),
            review.getUser().getEmail()
        );
        dto.setUser(userDto);
        
        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());
        dto.setCreatedAt(Date.from(review.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        return dto;
    }

    /**
     * Get reviews by a specific user
     * 
     * @param userId user ID
     * @param pageable pagination information
     * @return page of reviews
     */
    @Transactional(readOnly = true)
    public Page<Review> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdWithBookAndUser(userId, pageable);
    }

    /**
     * Create a new review
     * 
     * @param bookId book ID
     * @param userId user ID
     * @param rating rating (1-5)
     * @param reviewText review text
     * @return created review
     * @throws IllegalArgumentException if validation fails
     */
    public Review createReview(Long bookId, Long userId, Integer rating, String reviewText) {
        // Validate inputs
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Check if book and user exist
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException("Book not found");
        }
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Book book = bookOptional.get();
        User user = userOptional.get();

        // Check if user already reviewed this book
        Optional<Review> existingReview = reviewRepository.findByUserIdAndBookId(userId, bookId);
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("User has already reviewed this book");
        }

        // Create new review
        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setReviewText(reviewText);

        // Save review
        review = reviewRepository.save(review);

        // Update book's average rating and review count
        updateBookRatingStatistics(book);

        return review;
    }

    /**
     * Update an existing review
     * 
     * @param reviewId review ID
     * @param userId user ID (for authorization)
     * @param rating new rating
     * @param reviewText new review text
     * @return updated review
     * @throws IllegalArgumentException if validation fails
     */
    public Review updateReview(Long reviewId, Long userId, Integer rating, String reviewText) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Find review
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (reviewOptional.isEmpty()) {
            throw new IllegalArgumentException("Review not found");
        }

        Review review = reviewOptional.get();

        // Check if user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to update this review");
        }

        // Update review
        review.setRating(rating);
        review.setReviewText(reviewText);
        review = reviewRepository.save(review);

        // Update book's rating statistics
        updateBookRatingStatistics(review.getBook());

        return review;
    }

    /**
     * Delete a review
     * 
     * @param reviewId review ID
     * @param userId user ID (for authorization)
     * @throws IllegalArgumentException if validation fails
     */
    public void deleteReview(Long reviewId, Long userId) {
        // Find review
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (reviewOptional.isEmpty()) {
            throw new IllegalArgumentException("Review not found");
        }

        Review review = reviewOptional.get();

        // Check if user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to delete this review");
        }

        Book book = review.getBook();

        // Delete review
        reviewRepository.delete(review);

        // Update book's rating statistics
        updateBookRatingStatistics(book);
    }

    /**
     * Get review by ID
     * 
     * @param id review ID
     * @return review if found
     */
    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    /**
     * Check if user has reviewed a specific book
     * 
     * @param bookId book ID
     * @param userId user ID
     * @return true if user has reviewed the book
     */
    @Transactional(readOnly = true)
    public boolean hasUserReviewedBook(Long bookId, Long userId) {
        return reviewRepository.existsByUserIdAndBookId(userId, bookId);
    }

    /**
     * Get user's review for a specific book
     * 
     * @param bookId book ID
     * @param userId user ID
     * @return user's review if exists
     */
    @Transactional(readOnly = true)
    public Optional<Review> getUserReviewForBook(Long bookId, Long userId) {
        return reviewRepository.findByUserIdAndBookId(userId, bookId);
    }

    /**
     * Update book's average rating and review count
     * 
     * @param book book to update
     */
    private void updateBookRatingStatistics(Book book) {
        // Calculate new average rating
        Double avgRating = reviewRepository.calculateAverageRatingForBook(book.getId());
        long reviewCount = reviewRepository.countByBookId(book.getId());
        
        if (avgRating != null) {
            book.setAverageRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
            book.setReviewCount((int) reviewCount);
        } else {
            // No reviews, reset to defaults
            book.setAverageRating(BigDecimal.ZERO);
            book.setReviewCount(0);
        }
        
        bookRepository.save(book);
    }
}
