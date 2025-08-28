package com.bookreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application class for BookReview platform
 * 
 * This application provides:
 * - RESTful API for book reviews and ratings
 * - JWT-based authentication and authorization
 * - AI-powered book recommendations using OpenAI
 * - User profile and favorites management
 * - Comprehensive book catalog with search functionality
 * 
 * @author BookReview Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class BookReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookReviewApplication.class, args);
    }
}
