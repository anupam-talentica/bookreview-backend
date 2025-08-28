package com.bookreview.repository;

import com.bookreview.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Book entity operations
 * 
 * Provides CRUD operations and custom queries for book management
 * 
 * @author BookReview Development Team
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Search books by title and author (case insensitive)
     * 
     * @param query the search query
     * @param pageable pagination information
     * @return page of books matching the search
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY b.averageRating DESC, b.reviewCount DESC")
    Page<Book> searchByTitleOrAuthor(@Param("query") String query, Pageable pageable);

    /**
     * Find books by title containing (case insensitive)
     * 
     * @param title the title to search for
     * @param pageable pagination information
     * @return page of books with matching titles
     */
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Find books by author containing (case insensitive)
     * 
     * @param author the author to search for
     * @param pageable pagination information
     * @return page of books with matching authors
     */
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    /**
     * Find books by genre containing
     * 
     * @param genre the genre to search for
     * @param pageable pagination information
     * @return page of books with matching genre
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.genres) LIKE LOWER(CONCAT('%', :genre, '%'))")
    Page<Book> findByGenreContaining(@Param("genre") String genre, Pageable pageable);

    /**
     * Find books published in a specific year
     * 
     * @param year the publication year
     * @param pageable pagination information
     * @return page of books published in the year
     */
    Page<Book> findByPublishedYear(Integer year, Pageable pageable);

    /**
     * Find top-rated books (rating >= threshold)
     * 
     * @param minRating minimum rating threshold
     * @param pageable pagination information
     * @return page of highly-rated books
     */
    @Query("SELECT b FROM Book b WHERE b.averageRating >= :minRating AND b.reviewCount >= 1 ORDER BY b.averageRating DESC, b.reviewCount DESC")
    Page<Book> findTopRatedBooks(@Param("minRating") BigDecimal minRating, Pageable pageable);

    /**
     * Find popular books (review count >= threshold)
     * 
     * @param minReviewCount minimum review count threshold
     * @param pageable pagination information
     * @return page of popular books
     */
    @Query("SELECT b FROM Book b WHERE b.reviewCount >= :minReviewCount ORDER BY b.reviewCount DESC, b.averageRating DESC")
    Page<Book> findPopularBooks(@Param("minReviewCount") Integer minReviewCount, Pageable pageable);

    /**
     * Find recently added books
     * 
     * @param pageable pagination information
     * @return page of recently added books
     */
    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC")
    Page<Book> findRecentlyAdded(Pageable pageable);

    /**
     * Find books by multiple criteria with full-text search
     * 
     * @param query search query
     * @param minRating minimum rating (optional)
     * @param minYear minimum publication year (optional)
     * @param maxYear maximum publication year (optional)
     * @param pageable pagination information
     * @return page of books matching criteria
     */
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:minRating IS NULL OR b.averageRating >= :minRating) " +
           "AND (:minYear IS NULL OR b.publishedYear >= :minYear) " +
           "AND (:maxYear IS NULL OR b.publishedYear <= :maxYear) " +
           "ORDER BY b.averageRating DESC, b.reviewCount DESC")
    Page<Book> findByCriteria(@Param("query") String query,
                             @Param("minRating") BigDecimal minRating,
                             @Param("minYear") Integer minYear,
                             @Param("maxYear") Integer maxYear,
                             Pageable pageable);

    /**
     * Get book statistics
     * 
     * @return list of book statistics [totalBooks, avgRating, totalReviews]
     */
    @Query("SELECT COUNT(b), AVG(b.averageRating), SUM(b.reviewCount) FROM Book b")
    List<Object[]> getBookStatistics();

    /**
     * Find books similar to given book (by genres and author)
     * 
     * @param bookId the book ID to find similar books for
     * @param author the author
     * @param genres the genres
     * @param pageable pagination information
     * @return page of similar books
     */
    @Query("SELECT b FROM Book b WHERE b.id != :bookId AND " +
           "(b.author = :author OR " +
           "LOWER(b.genres) LIKE LOWER(CONCAT('%', :genres, '%'))) " +
           "ORDER BY b.averageRating DESC")
    Page<Book> findSimilarBooks(@Param("bookId") Long bookId,
                               @Param("author") String author,
                               @Param("genres") String genres,
                               Pageable pageable);

    /**
     * Find books by title containing or author containing (case insensitive)
     * Used for AI recommendation matching
     * 
     * @param title the title to search for
     * @param author the author to search for
     * @return list of books matching either title or author
     */
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}
