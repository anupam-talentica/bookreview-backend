package com.bookreview.repository;

import com.bookreview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations
 * 
 * Provides CRUD operations and custom queries for user management
 * 
 * @author BookReview Development Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * 
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email address and active status
     * 
     * @param email the email address
     * @param active the active status
     * @return Optional containing the user if found
     */
    Optional<User> findByEmailAndActive(String email, Boolean active);

    /**
     * Check if user exists by email
     * 
     * @param email the email address
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if active user exists by email
     * 
     * @param email the email address
     * @return true if active user exists
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.active = true")
    boolean existsByEmailAndActive(@Param("email") String email);

    /**
     * Find all active users
     * 
     * @return list of active users
     */
    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.createdAt DESC")
    Iterable<User> findAllActiveUsers();

    /**
     * Count total active users
     * 
     * @return count of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();

    /**
     * Find users by name containing (case insensitive)
     * 
     * @param name the name to search for
     * @return list of users with matching names
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) AND u.active = true")
    Iterable<User> findByNameContainingIgnoreCase(@Param("name") String name);
}
