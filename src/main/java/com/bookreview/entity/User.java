package com.bookreview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing registered users of the BookReview platform
 * 
 * Features:
 * - Unique email-based authentication
 * - Profile information (name, bio, avatar)
 * - Email verification status
 * - Account activation status
 * - Relationships with reviews and favorites
 * 
 * @author BookReview Development Team
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // One-to-Many relationship with Reviews
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> reviews = new HashSet<>();

    // Many-to-Many relationship with Books (favorites)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @JsonIgnore
    private Set<Book> favoriteBooks = new HashSet<>();

    // Constructors
    public User() {
    }

    public User(String email, String passwordHash, String name) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public Set<Book> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(Set<Book> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

    // Helper methods for managing relationships
    public void addReview(Review review) {
        reviews.add(review);
        review.setUser(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setUser(null);
    }

    public void addFavoriteBook(Book book) {
        favoriteBooks.add(book);
        book.getFavoritedByUsers().add(this);
    }

    public void removeFavoriteBook(Book book) {
        favoriteBooks.remove(book);
        book.getFavoritedByUsers().remove(this);
    }

    // Convenience methods
    public boolean isEmailVerified() {
        return Boolean.TRUE.equals(emailVerified);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    @JsonIgnore
    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }

    @JsonIgnore
    public int getFavoriteBooksCount() {
        return favoriteBooks != null ? favoriteBooks.size() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", emailVerified=" + emailVerified +
                ", active=" + active +
                ", reviewCount=" + getReviewCount() +
                ", favoriteBooksCount=" + getFavoriteBooksCount() +
                '}';
    }
}
