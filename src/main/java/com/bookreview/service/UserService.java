package com.bookreview.service;

import com.bookreview.dto.UserRegistrationDto;
import com.bookreview.entity.User;
import com.bookreview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for User management operations
 * 
 * Handles:
 * - User registration and validation
 * - Password encoding and verification
 * - User authentication
 * - Profile management
 * 
 * @author BookReview Development Team
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user
     * 
     * @param registrationDto user registration data
     * @return created user entity
     * @throws IllegalArgumentException if user already exists
     */
    public User registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + registrationDto.getEmail() + " already exists");
        }

        // Create new user entity
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setName(registrationDto.getName());
        user.setBio(registrationDto.getBio());
        
        // Encode password
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        user.setPasswordHash(encodedPassword);
        
        // Set default values
        user.setEmailVerified(false);
        user.setActive(true);

        // Save to database
        return userRepository.save(user);
    }

    /**
     * Authenticate user with email and password
     * 
     * @param email user email
     * @param password user password
     * @return authenticated user if credentials are valid
     * @throws IllegalArgumentException if credentials are invalid
     */
    public User authenticateUser(String email, String password) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmailAndActive(email, true);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = userOptional.get();
        
        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user;
    }

    /**
     * Find user by email
     * 
     * @param email user email
     * @return user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by ID
     * 
     * @param id user ID
     * @return user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by ID with reviews loaded
     * 
     * @param id user ID
     * @return user with reviews loaded if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByIdWithReviews(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Force loading of reviews to ensure accurate count
            user.getReviews().size();
            user.getFavoriteBooks().size();
        }
        return userOptional;
    }

    /**
     * Check if user exists by email
     * 
     * @param email user email
     * @return true if user exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Update user profile
     * 
     * @param user user entity to update
     * @return updated user
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Update user profile information
     * 
     * @param userId user ID
     * @param name new name
     * @param bio new bio
     * @param avatarUrl new avatar URL
     * @return updated user
     * @throws IllegalArgumentException if user not found
     */
    public User updateUserProfile(Long userId, String name, String bio, String avatarUrl) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        
        // Update profile fields
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }
        
        if (bio != null) {
            user.setBio(bio.trim().isEmpty() ? null : bio.trim());
        }
        
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl.trim().isEmpty() ? null : avatarUrl.trim());
        }

        return userRepository.save(user);
    }

    /**
     * Change user password
     * 
     * @param userId user ID
     * @param currentPassword current password
     * @param newPassword new password
     * @throws IllegalArgumentException if user not found or current password is incorrect
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Encode and set new password
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encodedNewPassword);
        
        userRepository.save(user);
    }

    /**
     * Deactivate user account
     * 
     * @param userId user ID
     */
    public void deactivateUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(false);
            userRepository.save(user);
        }
    }

    /**
     * Verify user email
     * 
     * @param userId user ID
     */
    public void verifyUserEmail(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmailVerified(true);
            userRepository.save(user);
        }
    }

    /**
     * Count total active users
     * 
     * @return count of active users
     */
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
}
