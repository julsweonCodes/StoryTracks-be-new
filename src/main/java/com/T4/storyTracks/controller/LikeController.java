package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.idempotency.Idempotent;
import com.T4.storyTracks.idempotency.IdempotencyEndpoint;
import com.T4.storyTracks.service.JWTService;
import com.T4.storyTracks.service.LikeService;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final JWTService jwtService;

    @PostMapping("/{postId}/like")
    @Idempotent(endpoint = IdempotencyEndpoint.POSTS_LIKE, pathTemplate = "/api/v1/posts/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = jwtService.requireUserId(authHeader);
        likeService.likePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("Post liked", null));
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @PathVariable Long postId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = jwtService.requireUserId(authHeader);
        likeService.unlikePost(postId, userId);

        return ResponseEntity.ok(ApiResponse.success("Post unliked", null));
    }

}
