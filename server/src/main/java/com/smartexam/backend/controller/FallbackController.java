package com.smartexam.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Fallback controller for SPA routing
 * Handles all unmatched paths by forwarding to index.html
 * This allows Vue Router to handle client-side routing properly
 */
@Controller
public class FallbackController {
    
    /**
     * Forward all unmatched non-API paths to index.html
     * This handles client-side routing for Vue.js
     */
    @GetMapping(value = {"", "/", "/home", "/about", "/login", "/register", "/dashboard"})
    public String fallbackToIndex() {
        // Directly return index.html for known routes
        return "forward:/index.html";
    }
    
    /**
     * Catch-all for any other non-API paths
     */
    @GetMapping(value = "/**", params = "!_spring-security-filter-chain")
    public String catchAllFallback(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        
        // Exclude API paths completely
        if (requestURI.startsWith("/api/")) {
            return null;
        }
        
        // Exclude static resources with extensions
        if (requestURI.contains(".")) {
            return null;
        }
        
        // Exclude Spring Boot actuator endpoints if any
        if (requestURI.startsWith("/actuator/")) {
            return null;
        }
        
        // Forward to index.html for client-side routing
        return "forward:/index.html";
    }
}