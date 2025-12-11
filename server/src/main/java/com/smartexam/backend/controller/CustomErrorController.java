package com.smartexam.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Custom Error Controller to handle all errors
 * Prevents infinite loops by providing a clear error handling mechanism
 */
@Controller
public class CustomErrorController {
    
    private static final String PATH = "/error";
    
    @RequestMapping(value = PATH)
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        
        // Get error attributes
        Map<String, Object> errorAttributes = getErrorAttributes(request);
        
        // Set response status
        Integer statusCode = (Integer) errorAttributes.get("status");
        response.setStatus(statusCode != null ? statusCode : 500);
        
        // Add error attributes to model
        modelAndView.addObject("error", errorAttributes.get("error"));
        modelAndView.addObject("status", statusCode);
        modelAndView.addObject("message", errorAttributes.get("message"));
        modelAndView.addObject("path", errorAttributes.get("path"));
        
        // For API requests, return JSON error response
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/")) {
            // For API requests, let Spring's default JSON error handling take over
            return null;
        }
        
        // For SPA requests, redirect to index.html with error query parameter
        // This allows Vue Router to handle the error on the client side
        return new ModelAndView("redirect:/index.html?error=" + statusCode);
    }
    
    /**
     * Get error attributes from request
     */
    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        Map<String, Object> errorAttributes = new java.util.HashMap<>();
        
        // Extract error information from request attributes
        errorAttributes.put("status", request.getAttribute("javax.servlet.error.status_code"));
        errorAttributes.put("error", request.getAttribute("javax.servlet.error.error_message"));
        errorAttributes.put("message", request.getAttribute("javax.servlet.error.message"));
        errorAttributes.put("path", request.getAttribute("javax.servlet.error.request_uri"));
        errorAttributes.put("exception", request.getAttribute("javax.servlet.error.exception"));
        
        return errorAttributes;
    }
    

}