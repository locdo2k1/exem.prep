package com.example.exam.prep.exception;

import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler to handle all uncaught exceptions across the application
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles all uncaught exceptions
     * @param ex The exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex, WebRequest request) {
        // In production, you might want to log the full exception
        // logger.error("An unexpected error occurred: ", ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later.", 
                                     HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
