package com.smartexam.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Fallback controller for SPA routing
 * Handles all unmatched paths by forwarding to index.html
 * This allows Vue Router to handle client-side routing properly
 */
@Controller
public class FallbackController {
    
    /**
     * Forward all unmatched paths to index.html
     * Explicitly exclude API paths and static resources
     */
    @RequestMapping(value = "*", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String fallback(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        
        // Exclude API paths
        if (requestURI.startsWith("/api/")) {
            return null; // Let the container handle API 404s
        }
        
        // Exclude static resources with extensions
        if (requestURI.contains(".")) {
            return null; // Let the container handle static file 404s
        }
        
        // Exclude known static resource directories
        if (requestURI.startsWith("/css/") || requestURI.startsWith("/js/") || 
            requestURI.startsWith("/img/") || requestURI.startsWith("/fonts/") ||
            requestURI.equals("/favicon.ico")) {
            return null;
        }
        
        // Forward everything else to index.html for Vue Router to handle
        return "forward:/index.html";
    }
}