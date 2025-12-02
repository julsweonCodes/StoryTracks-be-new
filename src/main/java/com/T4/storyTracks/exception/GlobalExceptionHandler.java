package com.T4.storyTracks.exception;

import com.T4.storyTracks.common.ApiResponse;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 🧱 centralized exception handling for all controllers that use @RestController
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 Custom "not found" exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("404", ex.getMessage()));
    }

    // 🔹 Validation or bad request exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("400", ex.getMessage()));
    }

    @ExceptionHandler(JWTAuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtAuthError(JWTAuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("401", ex.getMessage()));
    }

    // 🔹 Catch-all for unhandled exceptions (shows message for debug)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("500", "Internal Server Error: " + ex.getMessage()));
    }


}
