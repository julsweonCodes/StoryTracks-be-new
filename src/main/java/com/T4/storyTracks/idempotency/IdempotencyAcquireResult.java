package com.T4.storyTracks.idempotency;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result of attempting to acquire idempotency lock.
 */
@Getter
@AllArgsConstructor
public class IdempotencyAcquireResult {

  public enum State {
    LOCK_ACQUIRED, // First request - proceed with business logic
    PROCESSING, // Another request is currently processing
    DONE // Previously completed - return cached result
  }

  private final State state;
  private final StoredResult storedResult;

  public static IdempotencyAcquireResult lockAcquired() {
    return new IdempotencyAcquireResult(State.LOCK_ACQUIRED, null);
  }

  public static IdempotencyAcquireResult processing() {
    return new IdempotencyAcquireResult(State.PROCESSING, null);
  }

  public static IdempotencyAcquireResult done(StoredResult result) {
    return new IdempotencyAcquireResult(State.DONE, result);
  }

  public boolean isLockAcquired() {
    return state == State.LOCK_ACQUIRED;
  }

  public boolean isProcessing() {
    return state == State.PROCESSING;
  }

  public boolean isDone() {
    return state == State.DONE;
  }
}
