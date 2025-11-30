package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.request.PostCreateRequest;
import com.T4.storyTracks.dto.request.UserIdRequest;
import com.T4.storyTracks.dto.response.PostDetailResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.service.PostService;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts") // Base URL Path for this controller
//@CrossOrigin(origins = "*") // Allows requests from all origins (for frontend integration).
public class PostController {

    private final PostService postsService;

    /**
     * Constructor-based dependency injection (DI). Spring injects an instance of PostsService
     * automatically.
     */
    public PostController(PostService postsService) {
        this.postsService = postsService;
    }

    /**
     * GET endpoint to retrieve all posts. URL: GET /api/v1/posts/feed
     *
     * @return List of all posts in JSON format.
     */
    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> getPosts(
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(postsService.getAllPosts(page));
    }

    @GetMapping("/feed/marker")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsByMarker(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int level,
            @RequestParam(defaultValue = "0") int page) {

        Page<PostResponse> posts = postsService.getPostsByMarker(lat, lng, level, page);
        return ResponseEntity.ok(ApiResponse.success("Successfully fetched posts", posts));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createPost(
            @RequestBody PostCreateRequest req) {
        System.out.println("👉 Received userId: " + req.getUserId());
        Long postId = postsService.createPost(req);

        return ResponseEntity.ok(
                ApiResponse.success("Post created successfully", Map.of("postId", postId)));
    }

    /**
     * GET /api/v1/posts/{id} Fetch a single post by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostById(@PathVariable Long id) {
        PostDetailResponse post = postsService.getPostById(id);
        return ResponseEntity.ok(ApiResponse.success("Success", post));
    }

    /**
     * PUT /api/v1/posts/{id} Update a single post by ID (only the owner)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePostById(
            @PathVariable Long id,
            @RequestBody PostCreateRequest req
    ) {
        PostDetailResponse updated  = postsService.updatePostById(id, req);
        return ResponseEntity.ok(ApiResponse.success("Post updated successfully", updated));
    }

    /**
     * DELETE /api/v1/posts/{postId}?userId={userId}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePostById(
            @PathVariable Long id,
            @RequestBody UserIdRequest req
    ) {
        postsService.deletePostById(id, req.getUserId());
        return ResponseEntity.ok(
                ApiResponse.success("Post deleted successfully", null)
        );
    }

}
