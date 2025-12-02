package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.request.PostCreateRequest;
import com.T4.storyTracks.dto.response.PostDetailResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.service.JWTService;
import com.T4.storyTracks.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JWTService jwtService;

    // ==========================================================
    // 1. GET ALL POSTS (Feed)
    // ==========================================================
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page
    ) {
        Long viewerId = jwtService.extractUserId(authHeader);
        Page<PostResponse> posts = postService.getAllPosts(page, viewerId);

        return ResponseEntity.ok(ApiResponse.success("Fetched posts", posts));
    }

    // ==========================================================
    // 2. GET POSTS BY MARKER (Map Feed)
    // ==========================================================
    @GetMapping("/marker")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsByMarker(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int level,
            @RequestParam(defaultValue = "0") int page
    ) {
        Long viewerId = jwtService.extractUserId(authHeader);

        Page<PostResponse> posts = postService.getPostsByMarker(lat, lng, level, page, viewerId);

        return ResponseEntity.ok(ApiResponse.success("Fetched marker posts", posts));
    }

    // ==========================================================
    // 3. GET POST DETAIL
    // ==========================================================
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId
    ) {
        Long viewerId = jwtService.extractUserId(authHeader);

        PostDetailResponse detail = postService.getPostById(postId, viewerId);

        return ResponseEntity.ok(ApiResponse.success("Fetched post detail", detail));
    }

    // ==========================================================
    // 4. CREATE POST
    // ==========================================================
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PostCreateRequest req
    ) {
        Long userId = jwtService.extractUserId(authHeader);

        Long postId = postService.createPost(userId, req);

        return ResponseEntity.ok(ApiResponse.success("Post created", postId));
    }

    // ==========================================================
    // 5. UPDATE POST
    // ==========================================================
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId,
            @RequestBody PostCreateRequest req
    ) {
        Long userId = jwtService.extractUserId(authHeader);

        PostDetailResponse updated = postService.updatePostById(userId, postId, req);

        return ResponseEntity.ok(ApiResponse.success("Post updated", updated));
    }

    // ==========================================================
    // 6. DELETE POST
    // ==========================================================
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId
    ) {
        Long userId = jwtService.extractUserId(authHeader);
        postService.deletePostById(postId, userId);

        return ResponseEntity.ok(ApiResponse.success("Post deleted", null));
    }
}
