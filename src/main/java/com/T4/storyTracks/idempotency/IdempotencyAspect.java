package com.T4.storyTracks.idempotency;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * Aspect that intercepts @Idempotent annotated methods and applies idempotency
 * logic.
 * 
 * Flow:
 * 1. Extract Idempotency-Key header (return 400 if missing)
 * 2. Extract authenticated user ID (or 0 for anonymous)
 * 3. Build Redis key: idem:{userId}:{method}:{normalizedPath}:{idempotencyKey}
 * 4. Attempt to acquire lock:
 * - LOCK_ACQUIRED: Execute business logic, store result
 * - DONE: Return cached response with HIT header
 * - PROCESSING: Return 202 Accepted
 * 5. Add Idempotency-Key-Status header (HIT/MISS)
 */
@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class IdempotencyAspect {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String IDEMPOTENCY_STATUS_HEADER = "Idempotency-Key-Status";

    private final IdempotencyService idempotencyService;
    private final JWTService jwtService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.T4.storyTracks.idempotency.Idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get request context
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("No request context available, proceeding without idempotency");
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        // Get annotation details
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent annotation = method.getAnnotation(Idempotent.class);
        IdempotencyEndpoint endpoint = annotation.endpoint();
        String pathTemplate = annotation.pathTemplate();
        boolean requireAuth = annotation.requireAuth();

        // 1. Validate Idempotency-Key header
        String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            log.warn("Missing Idempotency-Key header for endpoint: {}", pathTemplate);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("400", "Idempotency-Key header is required"));
        }

        // 2. Extract user ID
        Long actorUserId = 0L;
        if (requireAuth) {
            String authHeader = request.getHeader("Authorization");
            actorUserId = jwtService.extractUserIdOrNull(authHeader);
            if (actorUserId == null) {
                actorUserId = 0L;
            }
        }

        // 3. Build Redis key
        String httpMethod = request.getMethod();
        String redisKey = idempotencyService.buildKey(actorUserId, httpMethod, pathTemplate, idempotencyKey);
        log.debug("Idempotency key built: {}", redisKey);

        // 4. Attempt to acquire lock
        IdempotencyAcquireResult acquireResult;
        try {
            acquireResult = idempotencyService.acquire(redisKey, endpoint.getProcessingTtl());
        } catch (IdempotencyException e) {
            log.error("Idempotency service unavailable", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("503", "Service temporarily unavailable. Please retry."));
        }

        // 5. Handle based on state
        if (acquireResult.isDone()) {
            // Return cached result
            StoredResult stored = acquireResult.getStoredResult();
            if (response != null) {
                response.setHeader(IDEMPOTENCY_STATUS_HEADER, "HIT");
            }
            log.info("Idempotency HIT for key: {}", redisKey);
            return buildResponseFromStored(stored);
        }

        if (acquireResult.isProcessing()) {
            // Another request is processing
            if (response != null) {
                response.setHeader(IDEMPOTENCY_STATUS_HEADER, "PROCESSING");
            }
            log.info("Request already processing for key: {}", redisKey);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(ApiResponse.success("Request is being processed. Please wait.", null));
        }

        // LOCK_ACQUIRED - proceed with business logic
        try {
            Object result = joinPoint.proceed();

            // Store successful result
            if (result instanceof ResponseEntity<?> responseEntity) {
                Object body = responseEntity.getBody();
                int statusCode = responseEntity.getStatusCode().value();

                String bodyJson = objectMapper.writeValueAsString(body);
                StoredResult storedResult = new StoredResult(statusCode, bodyJson);
                idempotencyService.storeDone(redisKey, storedResult, endpoint.getDoneTtl());
            }

            if (response != null) {
                response.setHeader(IDEMPOTENCY_STATUS_HEADER, "MISS");
            }
            log.info("Idempotency MISS (executed) for key: {}", redisKey);
            return result;

        } catch (Throwable e) {
            // Clear lock on failure to allow retry
            idempotencyService.storeFailedOrClear(redisKey, e, true);
            throw e;
        }
    }

    /**
     * Reconstruct ResponseEntity from stored result.
     */
    private ResponseEntity<?> buildResponseFromStored(StoredResult stored) {
        try {
            Object body = objectMapper.readValue(stored.getBody(), Object.class);
            return ResponseEntity.status(stored.getStatusCode()).body(body);
        } catch (Exception e) {
            log.error("Failed to deserialize stored result", e);
            return ResponseEntity.status(stored.getStatusCode()).body(stored.getBody());
        }
    }
}
