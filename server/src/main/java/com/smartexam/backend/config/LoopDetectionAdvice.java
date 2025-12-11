package com.smartexam.backend.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller Advice to detect and prevent infinite loops in request processing
 * This adds a counter to requests and throws an exception if too many loops are detected
 */
@ControllerAdvice
public class LoopDetectionAdvice {
    
    private static final String LOOP_COUNTER = "__loop_counter__";
    private static final int MAX_LOOPS = 10; // Maximum allowed request loops
    
    /**
     * Check for infinite loops by counting request attribute sets
     * Throws IllegalStateException if too many loops are detected
     */
    @ModelAttribute
    public void checkForInfiniteLoop(HttpServletRequest request) {
        Integer counter = (Integer) request.getAttribute(LOOP_COUNTER);
        
        if (counter == null) {
            // First request, initialize counter
            request.setAttribute(LOOP_COUNTER, 1);
        } else if (counter >= MAX_LOOPS) {
            // Too many loops detected, throw exception
            String requestURI = request.getRequestURI();
            throw new IllegalStateException(
                String.format("Infinite loop detected for request: %s (loop count: %d)", 
                requestURI, counter)
            );
        } else {
            // Increment counter
            request.setAttribute(LOOP_COUNTER, counter + 1);
        }
    }
}