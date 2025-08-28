package com.bookreview.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Service for fetching book cover images from external APIs
 * 
 * Uses Open Library API which provides free book cover images
 * 
 * @author BookReview Development Team
 */
@Service
public class BookCoverService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BookCoverService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get book cover image URL for a given title and author
     * Uses Open Library API which is free and doesn't require API keys
     * 
     * @param title book title
     * @param author book author
     * @return cover image URL or default placeholder
     */
    public String getBookCoverUrl(String title, String author) {
        try {
            // First try to search by title and author
            String searchQuery = title + " " + author;
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            
            String searchUrl = UriComponentsBuilder
                .fromHttpUrl("https://openlibrary.org/search.json")
                .queryParam("q", encodedQuery)
                .queryParam("limit", "1")
                .queryParam("fields", "cover_i,title,author_name")
                .toUriString();

            ResponseEntity<String> response = restTemplate.getForEntity(searchUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode docs = jsonNode.get("docs");
                
                if (docs != null && docs.isArray() && docs.size() > 0) {
                    JsonNode firstBook = docs.get(0);
                    JsonNode coverId = firstBook.get("cover_i");
                    
                    if (coverId != null && !coverId.isNull()) {
                        // Open Library cover URL format: https://covers.openlibrary.org/b/id/{cover_id}-L.jpg
                        return "https://covers.openlibrary.org/b/id/" + coverId.asText() + "-L.jpg";
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching book cover for: " + title + " by " + author + " - " + e.getMessage());
        }
        
        // Return a generic book cover placeholder if no cover found
        return getDefaultBookCoverUrl(title, author);
    }

    /**
     * Get book cover image URL asynchronously
     * 
     * @param title book title
     * @param author book author
     * @return CompletableFuture with cover image URL
     */
    public CompletableFuture<String> getBookCoverUrlAsync(String title, String author) {
        return CompletableFuture.supplyAsync(() -> getBookCoverUrl(title, author));
    }

    /**
     * Generate a default book cover URL using a placeholder service
     * Creates a simple cover with title and author text
     * 
     * @param title book title
     * @param author book author
     * @return placeholder cover image URL
     */
    private String getDefaultBookCoverUrl(String title, String author) {
        try {
            // Use a placeholder service that generates book covers with text
            String encodedTitle = URLEncoder.encode(title.length() > 30 ? title.substring(0, 30) + "..." : title, StandardCharsets.UTF_8);
            String encodedAuthor = URLEncoder.encode(author.length() > 20 ? author.substring(0, 20) + "..." : author, StandardCharsets.UTF_8);
            
            // Using placeholder.com to generate a simple book cover
            return String.format("https://placehold.co/300x400?text=%s%%0A%%0ABy:%s", 
                                encodedTitle, encodedAuthor);
        } catch (Exception e) {
            // Final fallback - just a generic book placeholder
            return "https://via.placeholder.com/300x450/2C3E50/FFFFFF?text=Book+Cover";
        }
    }

    /**
     * Check if the service is available
     * 
     * @return true if the service can fetch covers
     */
    public boolean isAvailable() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("https://openlibrary.org/", String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
