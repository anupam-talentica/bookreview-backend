package com.bookreview.repository;

import com.bookreview.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity operations
 * 
 * Provides CRUD operations and custom queries for review management
 * 
 * @author BookReview Development Team
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find review by user and book
     * 
     * @param userId the user ID
     * @param bookId the book ID
     * @return Optional containing the review if found
     */
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.book.id = :bookId")
    Optional<Review> findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Find all reviews by user
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of reviews by the user
     */
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<Review> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find all reviews for a book
     * 
     * @param bookId the book ID
     * @param pageable pagination information
     * @return page of reviews for the book
     */
    @Query("SELECT r FROM Review r WHERE r.book.id = :bookId ORDER BY r.createdAt DESC")
    Page<Review> findByBookId(@Param("bookId") Long bookId, Pageable pageable);

    /**
     * Find reviews by rating
     * 
     * @param rating the rating value
     * @param pageable pagination information
     * @return page of reviews with the specified rating
     */
    Page<Review> findByRating(Integer rating, Pageable pageable);

    /**
     * Find reviews by rating range
     * 
     * @param minRating minimum rating
     * @param maxRating maximum rating
     * @param pageable pagination information
     * @return page of reviews within the rating range
     */
    @Query("SELECT r FROM Review r WHERE r.rating BETWEEN :minRating AND :maxRating ORDER BY r.createdAt DESC")
    Page<Review> findByRatingBetween(@Param("minRating") Integer minRating, 
                                   @Param("maxRating") Integer maxRating, 
                                   Pageable pageable);

    /**
     * Find recent reviews with text
     * 
     * @param pageable pagination information
     * @return page of recent reviews that have text
     */
    @Query("SELECT r FROM Review r WHERE r.reviewText IS NOT NULL AND TRIM(r.reviewText) != '' ORDER BY r.createdAt DESC")
    Page<Review> findRecentReviewsWithText(Pageable pageable);

    /**
     * Count reviews by user
     * 
     * @param userId the user ID
     * @return number of reviews by the user
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Count reviews for a book
     * 
     * @param bookId the book ID
     * @return number of reviews for the book
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId")
    long countByBookId(@Param("bookId") Long bookId);

    /**
     * Calculate average rating for a book
     * 
     * @param bookId the book ID
     * @return average rating for the book
     */
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.book.id = :bookId")
    Double calculateAverageRatingForBook(@Param("bookId") Long bookId);

    /**
     * Get rating distribution for a book
     * 
     * @param bookId the book ID
     * @return list of [rating, count] pairs
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.book.id = :bookId GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionForBook(@Param("bookId") Long bookId);

    /**
     * Get user's average rating given
     * 
     * @param userId the user ID
     * @return user's average rating
     */
    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.user.id = :userId")
    Double getUserAverageRating(@Param("userId") Long userId);

    /**
     * Find top reviewers (users with most reviews)
     * 
     * @param limit the number of top reviewers to return
     * @return list of [user, reviewCount] pairs
     */
    @Query("SELECT r.user, COUNT(r) as reviewCount FROM Review r " +
           "GROUP BY r.user " +
           "ORDER BY reviewCount DESC")
    Page<Object[]> findTopReviewers(Pageable pageable);

    /**
     * Check if user has already reviewed a book
     * 
     * @param userId the user ID
     * @param bookId the book ID
     * @return true if user has reviewed the book
     */
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.user.id = :userId AND r.book.id = :bookId")
    boolean existsByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Find reviews by book with pagination and user info
     * 
     * @param bookId the book ID
     * @param pageable pagination information
     * @return page of reviews with user information
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.book.id = :bookId ORDER BY r.createdAt DESC")
    Page<Review> findByBookIdWithUser(@Param("bookId") Long bookId, Pageable pageable);

    /**
     * Find reviews by user with pagination and book info
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of reviews with book information
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.book WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<Review> findByUserIdWithBook(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find reviews by user with pagination and both user and book info
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of reviews with both user and book information
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.book JOIN FETCH r.user WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<Review> findByUserIdWithBookAndUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Delete all reviews by user
     * 
     * @param userId the user ID
     * @return number of reviews deleted
     */
    @Modifying
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * Delete all reviews for a book
     * 
     * @param bookId the book ID
     * @return number of reviews deleted
     */
    @Modifying
    @Query("DELETE FROM Review r WHERE r.book.id = :bookId")
    int deleteByBookId(@Param("bookId") Long bookId);
}
