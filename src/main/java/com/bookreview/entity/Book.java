package com.bookreview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Book entity representing books in the BookReview platform
 * 
 * Features:
 * - Complete book information (title, author, description, etc.)
 * - Automatic rating calculation and review count
 * - Genre classification and publication year
 * - Cover image URL for display
 * - Relationships with reviews and user favorites
 * 
 * @author BookReview Development Team
 */
@Entity
@Table(name = "books")
public class Book extends BaseEntity {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author cannot exceed 255 characters")
    @Column(name = "author", nullable = false)
    private String author;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 500, message = "Cover image URL cannot exceed 500 characters")
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Size(max = 255, message = "Genres cannot exceed 255 characters")
    @Column(name = "genres")
    private String genres;

    @Min(value = 1000, message = "Published year must be at least 1000")
    @Max(value = 2030, message = "Published year cannot be in the future")
    @Column(name = "published_year")
    private Integer publishedYear;

    @DecimalMin(value = "0.00", message = "Average rating must be at least 0.00")
    @DecimalMax(value = "5.00", message = "Average rating cannot exceed 5.00")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Min(value = 0, message = "Review count must be non-negative")
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    // One-to-Many relationship with Reviews
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> reviews = new HashSet<>();

    // Many-to-Many relationship with Users (favorited by)
    @ManyToMany(mappedBy = "favoriteBooks", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> favoritedByUsers = new HashSet<>();

    // Constructors
    public Book() {
    }

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public Book(String title, String author, String description, String genres, Integer publishedYear) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.genres = genres;
        this.publishedYear = publishedYear;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public Set<User> getFavoritedByUsers() {
        return favoritedByUsers;
    }

    public void setFavoritedByUsers(Set<User> favoritedByUsers) {
        this.favoritedByUsers = favoritedByUsers;
    }

    // Helper methods for managing relationships
    public void addReview(Review review) {
        reviews.add(review);
        review.setBook(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setBook(null);
    }

    public void addFavoritedByUser(User user) {
        favoritedByUsers.add(user);
        user.getFavoriteBooks().add(this);
    }

    public void removeFavoritedByUser(User user) {
        favoritedByUsers.remove(user);
        user.getFavoriteBooks().remove(this);
    }

    // Convenience methods
    public boolean hasReviews() {
        return reviewCount != null && reviewCount > 0;
    }

    @JsonIgnore
    public boolean isFavoritedByUser(User user) {
        return favoritedByUsers.contains(user);
    }

    @JsonIgnore
    public int getFavoriteCount() {
        return favoritedByUsers != null ? favoritedByUsers.size() : 0;
    }

    public String[] getGenreArray() {
        if (genres == null || genres.trim().isEmpty()) {
            return new String[0];
        }
        return genres.split(",");
    }

    public boolean hasGenre(String genre) {
        if (genres == null || genre == null) {
            return false;
        }
        String[] genreArray = getGenreArray();
        for (String g : genreArray) {
            if (g.trim().equalsIgnoreCase(genre.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean isHighlyRated() {
        return averageRating != null && averageRating.compareTo(new BigDecimal("4.0")) >= 0;
    }

    public boolean isPopular() {
        return reviewCount != null && reviewCount >= 10;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genres='" + genres + '\'' +
                ", publishedYear=" + publishedYear +
                ", averageRating=" + averageRating +
                ", reviewCount=" + reviewCount +
                ", favoriteCount=" + getFavoriteCount() +
                '}';
    }
}
