package com.T4.storyTracks.idempotency;

import java.time.Duration;

/**
 * Endpoint-specific TTL policy for idempotency.
 * 
 * TTL values:
 * - USERS_REGISTER: 24 hours (important to prevent duplicate accounts)
 * - S3_UPLOAD_PROFILE: 1 hour (file uploads)
 * - POSTS_CREATE: 24 hours (prevent duplicate posts)
 * - POSTS_LIKE: 5 minutes (toggle-like operations)
 * - USERS_FOLLOW: 10 minutes (toggle-follow operations)
 * - AI_GENERATE: 10 minutes (external API calls, paid)
 */
public enum IdempotencyEndpoint {

  USERS_REGISTER(Duration.ofHours(24), Duration.ofSeconds(60)),
  S3_UPLOAD_PROFILE(Duration.ofHours(1), Duration.ofSeconds(120)),
  S3_UPLOAD_BLOG_IMAGES(Duration.ofHours(1), Duration.ofSeconds(120)),
  POSTS_CREATE(Duration.ofHours(24), Duration.ofSeconds(60)),
  POSTS_LIKE(Duration.ofMinutes(5), Duration.ofSeconds(30)),
  USERS_FOLLOW(Duration.ofMinutes(10), Duration.ofSeconds(30)),
  AI_GENERATE(Duration.ofMinutes(10), Duration.ofSeconds(120));

  private final Duration doneTtl;
  private final Duration processingTtl;

  IdempotencyEndpoint(Duration doneTtl, Duration processingTtl) {
    this.doneTtl = doneTtl;
    this.processingTtl = processingTtl;
  }

  public Duration getDoneTtl() {
    return doneTtl;
  }

  public Duration getProcessingTtl() {
    return processingTtl;
  }
}
