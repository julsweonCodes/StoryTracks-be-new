package com.T4.storyTracks.idempotency;

import java.lang.annotation.*;

/**
 * Marks a controller method as idempotent.
 * The aspect will handle:
 * 1. Validating Idempotency-Key header presence
 * 2. Building Redis key with normalized path
 * 3. Acquiring lock and checking existing results
 * 4. Storing results after successful execution
 * 
 * Usage:
 * @Idempotent(
 * endpoint = IdempotencyEndpoint.POSTS_CREATE,
 * pathTemplate = "/api/v1/posts/create"
 * )
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * The endpoint identifier for TTL policy lookup.
     */
    IdempotencyEndpoint endpoint();

    /**
     * The path template for normalization.
     * Use placeholders like {postId}, {userId} for path variables.
     * Example: "/api/v1/posts/{postId}/like"
     */
    String pathTemplate();

    /**
     * Whether the endpoint requires authentication.
     * If false, actorUserId will be 0 (anonymous).
     * Default: true
     */
    boolean requireAuth() default true;
}
