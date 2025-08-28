package com.bookreview.service;

import com.bookreview.entity.Book;
import com.bookreview.entity.User;
import com.bookreview.repository.BookRepository;
import com.bookreview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.PageImpl;
import com.bookreview.dto.BookDetailsDto;
import com.bookreview.dto.ReviewDto;
import java.time.ZoneId;

/**
 * Service class for Book management operations
 * 
 * Handles:
 * - Book retrieval with proper relationship loading
 * - Search operations
 * - Favorites management
 * - Book statistics
 * 
 * @author BookReview Development Team
 */
@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final BookCoverService bookCoverService;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository, OpenAIService openAIService, BookCoverService bookCoverService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.openAIService = openAIService;
        this.bookCoverService = bookCoverService;
    }

    /**
     * Get book by ID with properly loaded relationships
     * 
     * @param id book ID
     * @return book with loaded relationships
     */
    @Transactional(readOnly = true)
    public Optional<BookDetailsDto> getBookById(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            BookDetailsDto bookDetailsDto = new BookDetailsDto();
            bookDetailsDto.setId(book.getId());
            bookDetailsDto.setTitle(book.getTitle());
            bookDetailsDto.setAuthor(book.getAuthor());
            bookDetailsDto.setDescription(book.getDescription());
            bookDetailsDto.setCoverImageUrl(book.getCoverImageUrl());
            bookDetailsDto.setGenres(book.getGenres());
            bookDetailsDto.setPublishedYear(book.getPublishedYear());
            bookDetailsDto.setAverageRating(book.getAverageRating());
            bookDetailsDto.setReviewCount(book.getReviewCount());

            // Convert reviews to DTOs
            List<ReviewDto> reviewDtos = new ArrayList<>();
            book.getReviews().forEach(review -> {
                ReviewDto reviewDto = new ReviewDto();
                reviewDto.setId(review.getId());
                
                // Create nested user object
                ReviewDto.UserDto userDto = new ReviewDto.UserDto(
                    review.getUser().getId(),
                    review.getUser().getName(),
                    review.getUser().getEmail()
                );
                reviewDto.setUser(userDto);
                
                reviewDto.setRating(review.getRating());
                reviewDto.setReviewText(review.getReviewText());
                // Convert LocalDateTime to Date
                reviewDto.setCreatedAt(Date.from(review.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
                reviewDtos.add(reviewDto);
            });
            bookDetailsDto.setReviews(reviewDtos);

            return Optional.of(bookDetailsDto);
        }
        return Optional.empty();
    }

    /**
     * Get all books with pagination
     * 
     * @param pageable pagination information
     * @return page of books
     */
    @Transactional(readOnly = true)
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    /**
     * Search books by title or author
     * 
     * @param query search query
     * @param pageable pagination information
     * @return page of search results
     */
    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String query, Pageable pageable) {
        return bookRepository.searchByTitleOrAuthor(query, pageable);
    }

    /**
     * Get top-rated books
     * 
     * @param minRating minimum rating threshold
     * @param pageable pagination information
     * @return page of top-rated books
     */
    @Transactional(readOnly = true)
    public Page<Book> getTopRatedBooks(BigDecimal minRating, Pageable pageable) {
        return bookRepository.findTopRatedBooks(minRating, pageable);
    }

    /**
     * Get popular books (most reviewed)
     * 
     * @param minReviewCount minimum review count
     * @param pageable pagination information
     * @return page of popular books
     */
    @Transactional(readOnly = true)
    public Page<Book> getPopularBooks(int minReviewCount, Pageable pageable) {
        return bookRepository.findPopularBooks(minReviewCount, pageable);
    }

    /**
     * Get recently added books
     * 
     * @param pageable pagination information
     * @return page of recent books
     */
    @Transactional(readOnly = true)
    public Page<Book> getRecentBooks(Pageable pageable) {
        return bookRepository.findRecentlyAdded(pageable);
    }

    /**
     * Add book to user's favorites
     * 
     * @param bookId book ID
     * @param userId user ID
     * @return true if successfully added, false if already in favorites
     */
    public boolean addToFavorites(Long bookId, Long userId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (bookOptional.isPresent() && userOptional.isPresent()) {
            Book book = bookOptional.get();
            User user = userOptional.get();

            // Check if already in favorites
            if (!user.getFavoriteBooks().contains(book)) {
                user.addFavoriteBook(book);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove book from user's favorites
     * 
     * @param bookId book ID
     * @param userId user ID
     * @return true if successfully removed, false if not in favorites
     */
    public boolean removeFromFavorites(Long bookId, Long userId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (bookOptional.isPresent() && userOptional.isPresent()) {
            Book book = bookOptional.get();
            User user = userOptional.get();

            // Check if in favorites
            if (user.getFavoriteBooks().contains(book)) {
                user.removeFavoriteBook(book);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Check if book is in user's favorites
     * 
     * @param bookId book ID
     * @param userId user ID
     * @return true if book is favorited by user
     */
    @Transactional(readOnly = true)
    public boolean isBookFavorited(Long bookId, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getFavoriteBooks().stream()
                    .anyMatch(book -> book.getId().equals(bookId));
        }
        return false;
    }

    /**
     * Get total count of books
     * 
     * @return total book count
     */
    @Transactional(readOnly = true)
    public long getTotalBookCount() {
        return bookRepository.count();
    }

    /**
     * Get user's favorite books
     * 
     * @param userId user ID
     * @param pageable pagination information
     * @return page of user's favorite books
     */
    @Transactional(readOnly = true)
    public Page<Book> getUserFavorites(Long userId, Pageable pageable) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Book> favoriteBooks = user.getFavoriteBooks();
            
            // Convert Set to List for pagination
            List<Book> favoriteBooksList = new ArrayList<>(favoriteBooks);
            
            // Sort by creation date (most recent first)
            favoriteBooksList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            
            // Apply pagination
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), favoriteBooksList.size());
            
            if (start > favoriteBooksList.size()) {
                return new PageImpl<>(new ArrayList<>(), pageable, favoriteBooksList.size());
            }
            
            List<Book> pageContent = favoriteBooksList.subList(start, end);
            return new PageImpl<>(pageContent, pageable, favoriteBooksList.size());
        }
        
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * Check if book exists
     * 
     * @param id book ID
     * @return true if book exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }

    /**
     * Get personalized recommendations for a user
     * 
     * @param userId user ID
     * @param limit number of recommendations to return
     * @return recommendations with explanations
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPersonalizedRecommendations(Long userId, int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> recommendations = new ArrayList<>();

        // Get user's favorite books and reviews to understand preferences
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Book> favoriteBooks = user.getFavoriteBooks();
            
            // Strategy 1: Recommend books similar to user's favorites
            Set<Book> recommendedBooks = new HashSet<>();
            for (Book favoriteBook : favoriteBooks) {
                Pageable pageable = PageRequest.of(0, 3);
                Page<Book> similarBooks = bookRepository.findSimilarBooks(
                    favoriteBook.getId(), 
                    favoriteBook.getAuthor(), 
                    favoriteBook.getGenres(), 
                    pageable
                );
                for (Book similarBook : similarBooks.getContent()) {
                    if (!favoriteBooks.contains(similarBook) && recommendedBooks.size() < limit / 2) {
                        recommendedBooks.add(similarBook);
                        recommendations.add(createRecommendationWithExplanation(
                            similarBook, 
                            "Similar to your favorite: " + favoriteBook.getTitle(),
                            "genre_similarity"
                        ));
                    }
                }
            }
            
            // Strategy 2: Recommend top-rated books in genres user likes
            if (recommendations.size() < limit && !favoriteBooks.isEmpty()) {
                String[] genres = favoriteBooks.stream()
                    .map(Book::getGenres)
                    .filter(g -> g != null && !g.isEmpty())
                    .flatMap(g -> Arrays.stream(g.split(",")))
                    .map(String::trim)
                    .distinct()
                    .toArray(String[]::new);
                    
                for (String genre : genres) {
                    if (recommendations.size() >= limit) break;
                    
                    Pageable pageable = PageRequest.of(0, 2);
                    Page<Book> genreBooks = bookRepository.findByGenreContaining(genre, pageable);
                    
                    for (Book book : genreBooks.getContent()) {
                        if (!favoriteBooks.contains(book) && 
                            recommendedBooks.stream().noneMatch(b -> b.getId().equals(book.getId())) &&
                            recommendations.size() < limit) {
                            recommendedBooks.add(book);
                            recommendations.add(createRecommendationWithExplanation(
                                book,
                                "Highly rated in " + genre,
                                "genre_trending"
                            ));
                        }
                    }
                }
            }
        }
        
        // Strategy 3: Fill remaining slots with top-rated books
        if (recommendations.size() < limit) {
            Pageable pageable = PageRequest.of(0, limit - recommendations.size());
            Page<Book> topRated = bookRepository.findTopRatedBooks(new BigDecimal("4.0"), pageable);
            
            for (Book book : topRated.getContent()) {
                if (recommendations.stream().noneMatch(r -> {
                    Object bookObj = r.get("book");
                    if (bookObj instanceof Book) {
                        return ((Book) bookObj).getId().equals(book.getId());
                    }
                    return false;
                })) {
                    recommendations.add(createRecommendationWithExplanation(
                        book,
                        "Top rated by the community",
                        "community_favorite"
                    ));
                }
            }
        }
        
        result.put("recommendations", recommendations);
        result.put("userId", userId);
        result.put("count", recommendations.size());
        result.put("refreshedAt", new Date());
        
        return result;
    }

    /**
     * Get AI-powered recommendations for a user based on their favorite books
     * 
     * @param userId user ID
     * @param limit number of recommendations to return
     * @return AI-generated recommendations with explanations
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAIRecommendations(Long userId, int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> recommendations = new ArrayList<>();

        // Get user's favorite books
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Book> favoriteBooks = user.getFavoriteBooks();
            
            if (!favoriteBooks.isEmpty()) {
                // Convert favorite books to string list for OpenAI
                List<String> favoriteBookTitles = favoriteBooks.stream()
                    .map(book -> book.getTitle() + " by " + book.getAuthor())
                    .limit(5) // Limit to avoid too long prompt
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                
                // Get AI recommendations based on user's favorites (but don't include the favorites themselves)
                List<String> aiRecommendations = openAIService.getBookRecommendations(favoriteBookTitles);
                
                // Process AI recommendations and find matching books in our database
                for (String aiRecommendation : aiRecommendations) {
                    if (recommendations.size() >= limit) break;
                    
                    // Parse title and author from AI recommendation
                    String[] parts = aiRecommendation.split(" by ");
                    if (parts.length >= 2) {
                        String title = parts[0].trim();
                        String author = parts[1].trim();
                        
                        // Try to find the book in our database
                        List<Book> matchingBooks = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                            title, author
                        );
                        
                        Book recommendedBook = null;
                        if (!matchingBooks.isEmpty()) {
                            // Find exact or best match
                            recommendedBook = matchingBooks.stream()
                                .filter(book -> book.getTitle().equalsIgnoreCase(title.trim()) ||
                                              book.getAuthor().equalsIgnoreCase(author.trim()))
                                .findFirst()
                                .orElse(matchingBooks.get(0));
                        } else {
                            // Create a placeholder book entry for books not in our database
                            recommendedBook = createPlaceholderBook(title, author);
                        }
                        
                        if (recommendedBook != null) {
                            // Skip if this book is already in user's favorites
                            final Book finalRecommendedBook = recommendedBook;
                            boolean isAlreadyFavorite = favoriteBooks.stream()
                                .anyMatch(favBook -> 
                                    favBook.getTitle().equalsIgnoreCase(finalRecommendedBook.getTitle()) &&
                                    favBook.getAuthor().equalsIgnoreCase(finalRecommendedBook.getAuthor())
                                );
                            
                            if (!isAlreadyFavorite) {
                                // Get explanation from OpenAI
                                String explanation = openAIService.getRecommendationExplanation(
                                    aiRecommendation, 
                                    favoriteBookTitles
                                );
                                
                                recommendations.add(createRecommendationWithExplanation(
                                    recommendedBook,
                                    explanation,
                                    "ai_recommendation"
                                ));
                            }
                        }
                    }
                }
            } else {
                // User has no favorites, provide general AI recommendations
                List<String> popularGenres = Arrays.asList("Mystery", "Romance", "Fantasy", "Science Fiction");
                List<String> aiRecommendations = openAIService.getBookRecommendations(
                    Arrays.asList("Popular " + popularGenres.get((int)(Math.random() * popularGenres.size())) + " books")
                );
                
                for (String aiRecommendation : aiRecommendations) {
                    if (recommendations.size() >= limit) break;
                    
                    String[] parts = aiRecommendation.split(" by ");
                    if (parts.length >= 2) {
                        String title = parts[0].trim();
                        String author = parts[1].trim();
                        Book recommendedBook = createPlaceholderBook(title, author);
                        
                        recommendations.add(createRecommendationWithExplanation(
                            recommendedBook,
                            "AI-recommended popular book in this genre",
                            "ai_recommendation"
                        ));
                    }
                }
            }
        }

        result.put("recommendations", recommendations);
        result.put("userId", userId);
        result.put("count", recommendations.size());
        result.put("refreshedAt", new Date());
        result.put("type", "ai_powered");
        
        return result;
    }

    /**
     * Create a placeholder book for AI recommendations not in our database
     */
    private Book createPlaceholderBook(String title, String author) {
        Book book = new Book();
        book.setId(-1L); // Use negative ID to indicate placeholder
        book.setTitle(title);
        book.setAuthor(author);
        book.setAverageRating(BigDecimal.ZERO);
        book.setReviewCount(0);
        book.setGenres("Various");
        book.setDescription("This book was recommended by AI but is not yet in our database.");
        
        // Fetch cover image for the book
        try {
            String coverUrl = bookCoverService.getBookCoverUrl(title, author);
            book.setCoverImageUrl(coverUrl);
        } catch (Exception e) {
            System.err.println("Error fetching cover for " + title + " by " + author + ": " + e.getMessage());
            // Set a default placeholder if cover fetch fails
            book.setCoverImageUrl("https://via.placeholder.com/300x450/2C3E50/FFFFFF?text=Book+Cover");
        }
        
        return book;
    }

    /**
     * Get books similar to a specific book
     * 
     * @param bookId book ID
     * @param pageable pagination information
     * @return list of similar books
     */
    @Transactional(readOnly = true)
    public List<Book> getSimilarBooks(Long bookId, Pageable pageable) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            Page<Book> similarBooks = bookRepository.findSimilarBooks(
                bookId, 
                book.getAuthor(), 
                book.getGenres(), 
                pageable
            );
            return similarBooks.getContent();
        }
        return new ArrayList<>();
    }

    /**
     * Create a recommendation object with explanation
     * 
     * @param book the recommended book
     * @param explanation why this book was recommended
     * @param type recommendation type
     * @return recommendation map
     */
    private Map<String, Object> createRecommendationWithExplanation(Book book, String explanation, String type) {
        Map<String, Object> recommendation = new HashMap<>();
        recommendation.put("book", book);
        recommendation.put("explanation", explanation);
        recommendation.put("type", type);
        recommendation.put("confidence", calculateConfidence(book, type));
        recommendation.put("recommendedAt", new Date());
        return recommendation;
    }

    /**
     * Calculate confidence score for a recommendation
     * 
     * @param book the recommended book
     * @param type recommendation type
     * @return confidence score (0.0 to 1.0)
     */
    private double calculateConfidence(Book book, String type) {
        double confidence = 0.5; // Base confidence
        
        // Boost confidence based on book quality
        BigDecimal rating = book.getAverageRating();
        if (rating.compareTo(new BigDecimal("4.5")) >= 0) confidence += 0.3;
        else if (rating.compareTo(new BigDecimal("4.0")) >= 0) confidence += 0.2;
        else if (rating.compareTo(new BigDecimal("3.5")) >= 0) confidence += 0.1;
        
        // Boost confidence based on review count
        if (book.getReviewCount() >= 100) confidence += 0.2;
        else if (book.getReviewCount() >= 50) confidence += 0.1;
        
        // Adjust based on recommendation type
        switch (type) {
            case "genre_similarity":
                confidence += 0.1;
                break;
            case "community_favorite":
                confidence += 0.05;
                break;
        }
        
        return Math.min(1.0, confidence);
    }
}
