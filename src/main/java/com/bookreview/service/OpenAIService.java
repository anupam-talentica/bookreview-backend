package com.bookreview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${app.openai.api-key}")
    private String apiKey;

    @Value("${app.openai.model:gpt-3.5-turbo}")
    private String model;

    @Value("${app.openai.max-tokens:150}")
    private int maxTokens;

    @Value("${app.openai.temperature:0.7}")
    private double temperature;

    @Value("${app.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Check if OpenAI service is properly configured and available
     */
    public boolean isAvailable() {
        return apiKey != null && 
               !apiKey.trim().isEmpty() && 
               !"your-openai-api-key".equals(apiKey.trim());
    }

    /**
     * Get book recommendations based on user's favorite books using OpenAI
     */
    public List<String> getBookRecommendations(List<String> favoriteBooks) {
        if (!isAvailable()) {
            logger.warn("OpenAI service is not available - API key not configured");
            return Collections.emptyList();
        }

        try {
            String prompt = buildRecommendationPrompt(favoriteBooks);
            String response = callOpenAI(prompt);
            return parseBookRecommendations(response);
        } catch (Exception e) {
            logger.error("Error getting book recommendations from OpenAI", e);
            return Collections.emptyList();
        }
    }

    /**
     * Build a prompt for OpenAI to recommend books based on user's favorites
     */
    private String buildRecommendationPrompt(List<String> favoriteBooks) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Based on someone who likes these books: ");
        
        for (int i = 0; i < favoriteBooks.size(); i++) {
            prompt.append("\"").append(favoriteBooks.get(i)).append("\"");
            if (i < favoriteBooks.size() - 1) {
                prompt.append(", ");
            }
        }
        
        prompt.append("\n\nPlease recommend 3 similar books that this person would enjoy. ");
        prompt.append("For each recommendation, provide the title and author in the format: ");
        prompt.append("'Title by Author'. One book per line. ");
        prompt.append("Only provide the book recommendations, no additional explanations.");
        
        return prompt.toString();
    }

    /**
     * Call OpenAI API with the given prompt
     */
    private String callOpenAI(String prompt) throws Exception {
        String url = baseUrl + "/chat/completions";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("OpenAI API call failed with status: " + response.getStatusCode());
        }

        return extractContentFromResponse(response.getBody());
    }

    /**
     * Extract content from OpenAI response
     */
    private String extractContentFromResponse(String responseBody) throws Exception {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode choicesNode = rootNode.get("choices");
        
        if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
            JsonNode firstChoice = choicesNode.get(0);
            JsonNode messageNode = firstChoice.get("message");
            JsonNode contentNode = messageNode.get("content");
            
            if (contentNode != null) {
                return contentNode.asText();
            }
        }
        
        throw new RuntimeException("Unable to extract content from OpenAI response");
    }

    /**
     * Parse book recommendations from OpenAI response
     */
    private List<String> parseBookRecommendations(String response) {
        List<String> recommendations = new ArrayList<>();
        
        String[] lines = response.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith("-")) {
                // Remove numbers from beginning of line (e.g., "1. ", "2. ")
                line = line.replaceFirst("^\\d+\\.\\s*", "");
                
                if (line.contains(" by ")) {
                    recommendations.add(line);
                }
            }
        }
        
        return recommendations;
    }

    /**
     * Get explanation for why a book was recommended based on user's favorites
     */
    public String getRecommendationExplanation(String recommendedBook, List<String> favoriteBooks) {
        if (!isAvailable()) {
            return "AI-powered recommendation";
        }

        try {
            String prompt = String.format(
                "Someone who likes %s might also enjoy \"%s\". In one short sentence, explain why this recommendation makes sense based on genre, theme, or style similarities.",
                String.join(", ", favoriteBooks),
                recommendedBook
            );
            
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error getting recommendation explanation from OpenAI", e);
            return String.format("Because you liked %s, you might enjoy this similar book.", 
                favoriteBooks.isEmpty() ? "your previous selections" : favoriteBooks.get(0));
        }
    }
}
