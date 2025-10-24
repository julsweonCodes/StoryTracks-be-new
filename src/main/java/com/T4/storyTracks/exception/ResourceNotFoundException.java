package com.T4.storyTracks.exception;


/**
 * Custom exception thrown when a requested resource (e.g., post) is not found.
 */
public class ResourceNotFoundException extends RuntimeException{
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
