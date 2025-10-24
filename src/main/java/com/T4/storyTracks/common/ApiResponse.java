package com.T4.storyTracks.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Unified API response wrapper that matches the project's front-end contract.
 * Used for both success and error responses.
 */

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
  private boolean success; // API call success status (true/false)
  private String code; // Response code (e.g., "200", "404", "500")
  private String message; // Description message
  private T data; // Actual data (or null for errors)

  // ✅ Static helper for success
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, "200", message, data);
  }

  // ✅ Static helper for error
  public static <T> ApiResponse<T> error(String code, String message) {
    return new ApiResponse<>(false, code, message, null);
  }
}