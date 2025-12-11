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
     * This handles all paths that aren't already matched by other controllers
     * API paths are excluded by SecurityConfig and other controllers
     */
    @RequestMapping(value = "/**", method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public String fallback(HttpServletRequest request) {
        // Get the path info to determine if it's a file with extension
        String pathInfo = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        
        // If the path has a file extension, don't forward - let it return 404
        // This prevents forwarding for non-existent static files
        if (pathInfo != null && pathInfo.contains(".")) {
            return null; // Return null to let the container handle 404 for files
        }
        
        // Forward everything else to index.html for Vue Router to handle
        return "forward:/index.html";
    }
}