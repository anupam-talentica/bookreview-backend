package com.bookreview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Review entity representing user reviews and ratings for books
 * 
 * Features:
 * - 1-5 star rating system
 * - Optional text review (up to 2000 characters)
 * - One review per user per book constraint
 * - Automatic book rating recalculation via database triggers
 * - Timestamps for creation and updates
 * 
 * @author BookReview Development Team
 */
@Entity
@Table(
    name = "reviews",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_reviews_user_book", 
        columnNames = {"user_id", "book_id"}
    )
)
public class Review extends BaseEntity {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Size(max = 2000, message = "Review text cannot exceed 2000 characters")
    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_user"))
    @JsonIgnoreProperties({"reviews", "favoriteBooks", "hibernateLazyInitializer", "handler"})
    private User user;

    // Many-to-One relationship with Book
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_book"))
    @JsonIgnoreProperties({"reviews", "hibernateLazyInitializer", "handler"})
    private Book book;

    // Constructors
    public Review() {
    }

    public Review(Integer rating, String reviewText, User user, Book book) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.user = user;
        this.book = book;
    }

    public Review(Integer rating, User user, Book book) {
        this.rating = rating;
        this.user = user;
        this.book = book;
    }

    // Getters and Setters
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    // Convenience methods
    public boolean hasReviewText() {
        return reviewText != null && !reviewText.trim().isEmpty();
    }

    public String getTruncatedReviewText(int maxLength) {
        if (reviewText == null || reviewText.length() <= maxLength) {
            return reviewText;
        }
        return reviewText.substring(0, maxLength - 3) + "...";
    }

    public boolean isPositiveReview() {
        return rating != null && rating >= 4;
    }

    public boolean isNegativeReview() {
        return rating != null && rating <= 2;
    }

    public boolean isNeutralReview() {
        return rating != null && rating == 3;
    }

    public String getRatingText() {
        if (rating == null) return "No rating";
        
        switch (rating) {
            case 1: return "Poor";
            case 2: return "Fair";
            case 3: return "Good";
            case 4: return "Very Good";
            case 5: return "Excellent";
            default: return "Unknown";
        }
    }

    public int getReviewTextLength() {
        return reviewText != null ? reviewText.length() : 0;
    }

    // Validation helper
    public boolean isValid() {
        return rating != null && rating >= 1 && rating <= 5 &&
               user != null && book != null;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + getId() +
                ", rating=" + rating +
                ", reviewTextLength=" + getReviewTextLength() +
                ", userId=" + (user != null ? user.getId() : null) +
                ", bookId=" + (book != null ? book.getId() : null) +
                ", createdAt=" + getCreatedAt() +
                '}';
    }

    // For JSON serialization (avoiding circular references)
    @JsonIgnore
    public String getUserName() {
        return user != null ? user.getName() : null;
    }

    @JsonIgnore
    public String getUserEmail() {
        return user != null ? user.getEmail() : null;
    }

    @JsonIgnore
    public String getBookTitle() {
        return book != null ? book.getTitle() : null;
    }

    @JsonIgnore
    public String getBookAuthor() {
        return book != null ? book.getAuthor() : null;
    }
}
