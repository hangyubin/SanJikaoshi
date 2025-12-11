package com.smartexam.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Fallback controller for SPA routing
 * Handles known routes by forwarding to index.html
 * This allows Vue Router to handle client-side routing properly
 */
@Controller
public class FallbackController {
    
    /**
     * Forward known SPA routes to index.html
     * These are the main routes used by the Vue.js application
     */
    @GetMapping(value = {
        "", "/", "/home", "/about", "/login", "/register", "/dashboard",
        "/exam", "/practice", "/profile", "/admin", "/subjects", "/questions"
    })
    public String fallbackToIndex() {
        // Directly return index.html for known routes
        // This prevents infinite loops by being explicit
        return "forward:/index.html";
    }
}