package com.T4.storyTracks.idempotency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a stored idempotency result.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoredResult {
  private int statusCode;
  private String body; // JSON string of the response body
}
