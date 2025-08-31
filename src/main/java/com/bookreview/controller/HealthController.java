package com.bookreview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://bookreview-frontend.s3-website.ap-south-1.amazonaws.com", "http://bookreview-frontend-staging.s3-website.ap-south-1.amazonaws.com"}, allowedHeaders = "*", allowCredentials = "true")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "UP");
        return ResponseEntity.ok(body);
    }
}


