package com.T4.storyTracks.idempotency;

/**
 * Exception thrown when idempotency cannot be guaranteed (e.g., Redis unavailable).
 * This triggers fail-closed behavior to prevent duplicate operations.
 */
public class IdempotencyException extends RuntimeException {
    
    public IdempotencyException(String message) {
        super(message);
    }
    
    public IdempotencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
