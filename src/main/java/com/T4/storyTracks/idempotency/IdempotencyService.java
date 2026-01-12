package com.T4.storyTracks.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * IdempotencyService manages idempotency keys in Redis with atomic lock/state
 * machine:
 * 
 * States:
 * - PROCESSING: Lock acquired, business logic executing
 * - DONE:{statusCode}:{jsonPayload}: Successfully completed
 * - FAILED:{errorMessage}: Failed (optional, for debugging)
 * 
 * Key format: idem:{actorUserId}:{httpMethod}:{normalizedPath}:{idempotencyKey}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  private static final String KEY_PREFIX = "idem:";
  private static final String STATE_PROCESSING = "PROCESSING";
  private static final String STATE_DONE = "DONE";
  private static final String STATE_FAILED = "FAILED";

  /**
   * Build the Redis key for idempotency.
   * Format: idem:{actorUserId}:{httpMethod}:{normalizedPath}:{idempotencyKey}
   */
  public String buildKey(Long actorUserId, String httpMethod, String normalizedPath, String idempotencyKey) {
    long userId = actorUserId != null ? actorUserId : 0L;
    return KEY_PREFIX + userId + ":" + httpMethod + ":" + normalizedPath + ":" + idempotencyKey;
  }

  /**
   * Normalize path by replacing path variables with placeholders.
   * e.g., /api/v1/posts/123/like -> /api/v1/posts/{postId}/like
   * /api/v1/users/456/follow -> /api/v1/users/{userId}/follow
   */
  public String normalizePath(String uri, String pathTemplate) {
    return pathTemplate;
  }

  /**
   * Attempt to acquire processing lock.
   * Uses SET NX EX for atomic lock acquisition.
   * 
   * @return IdempotencyAcquireResult with state information
   */
  public IdempotencyAcquireResult acquire(String redisKey, Duration processingTtl) {
    try {
      // Try to set PROCESSING state atomically (SET NX EX)
      Boolean acquired = redisTemplate.opsForValue()
          .setIfAbsent(redisKey, STATE_PROCESSING, processingTtl);

      if (Boolean.TRUE.equals(acquired)) {
        log.debug("Idempotency lock acquired for key: {}", redisKey);
        return IdempotencyAcquireResult.lockAcquired();
      }

      // Lock not acquired - check existing state
      Object existingValue = redisTemplate.opsForValue().get(redisKey);
      if (existingValue == null) {
        // Key expired between check, try again
        return acquire(redisKey, processingTtl);
      }

      String valueStr = existingValue.toString();

      if (STATE_PROCESSING.equals(valueStr)) {
        log.debug("Request already processing for key: {}", redisKey);
        return IdempotencyAcquireResult.processing();
      }

      if (valueStr.startsWith(STATE_DONE + ":")) {
        log.debug("Returning cached DONE result for key: {}", redisKey);
        return IdempotencyAcquireResult.done(parseStoredResult(valueStr));
      }

      if (valueStr.startsWith(STATE_FAILED + ":")) {
        log.debug("Previous request failed for key: {}", redisKey);
        // Delete failed key to allow retry
        redisTemplate.delete(redisKey);
        return acquire(redisKey, processingTtl);
      }

      log.warn("Unknown idempotency state for key {}: {}", redisKey, valueStr);
      return IdempotencyAcquireResult.lockAcquired();

    } catch (Exception e) {
      log.error("Redis error during idempotency acquire for key: {}", redisKey, e);
      // Fail-closed: return error to prevent duplicate operations
      throw new IdempotencyException("Redis unavailable, cannot guarantee idempotency", e);
    }
  }

  /**
   * Get stored result for a key (if exists and is DONE).
   */
  public Optional<StoredResult> getStoredResult(String redisKey) {
    try {
      Object value = redisTemplate.opsForValue().get(redisKey);
      if (value == null) {
        return Optional.empty();
      }

      String valueStr = value.toString();
      if (valueStr.startsWith(STATE_DONE + ":")) {
        return Optional.of(parseStoredResult(valueStr));
      }

      return Optional.empty();
    } catch (Exception e) {
      log.error("Redis error getting stored result for key: {}", redisKey, e);
      return Optional.empty();
    }
  }

  /**
   * Store successful result with DONE state.
   * Format: DONE:{statusCode}:{jsonPayload}
   */
  public void storeDone(String redisKey, StoredResult result, Duration doneTtl) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(result.getBody());
      String value = STATE_DONE + ":" + result.getStatusCode() + ":" + jsonPayload;

      redisTemplate.opsForValue().set(redisKey, value, doneTtl);
      log.debug("Stored DONE result for key: {}, ttl: {}", redisKey, doneTtl);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize result for key: {}", redisKey, e);
      // Still store with empty body
      String value = STATE_DONE + ":" + result.getStatusCode() + ":{}";
      redisTemplate.opsForValue().set(redisKey, value, doneTtl);
    }
  }

  /**
   * Store failed state or clear the key based on policy.
   */
  public void storeFailedOrClear(String redisKey, Throwable error, boolean clearOnFailure) {
    try {
      if (clearOnFailure) {
        redisTemplate.delete(redisKey);
        log.debug("Cleared failed key: {}", redisKey);
      } else {
        String value = STATE_FAILED + ":" + error.getMessage();
        redisTemplate.opsForValue().set(redisKey, value, Duration.ofMinutes(5));
        log.debug("Stored FAILED state for key: {}", redisKey);
      }
    } catch (Exception e) {
      log.error("Redis error storing failed state for key: {}", redisKey, e);
    }
  }

  /**
   * Clear a key (used for cleanup or allowing retry).
   */
  public void clear(String redisKey) {
    try {
      redisTemplate.delete(redisKey);
    } catch (Exception e) {
      log.error("Redis error clearing key: {}", redisKey, e);
    }
  }

  /**
   * Parse stored result from Redis value.
   * Format: DONE:{statusCode}:{jsonPayload}
   */
  private StoredResult parseStoredResult(String value) {
    // Remove "DONE:" prefix
    String rest = value.substring(STATE_DONE.length() + 1);

    int firstColon = rest.indexOf(':');
    if (firstColon == -1) {
      return new StoredResult(200, "{}");
    }

    int statusCode = Integer.parseInt(rest.substring(0, firstColon));
    String jsonPayload = rest.substring(firstColon + 1);

    return new StoredResult(statusCode, jsonPayload);
  }
}
