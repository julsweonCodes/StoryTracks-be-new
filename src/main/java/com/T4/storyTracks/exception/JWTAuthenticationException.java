package com.T4.storyTracks.exception;

public class JWTAuthenticationException extends RuntimeException {
    public JWTAuthenticationException(String message) {
        super(message);
    }
}