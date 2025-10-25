package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.PostDetailResponse;
import com.T4.storyTracks.dto.PostResponse;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.repository.PostRepository;
import com.T4.storyTracks.service.PostService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts") // Base URL Path for this controller
@CrossOrigin(origins = "*") // Allows requests from all origins (for frontend integration).
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
   * GET endpoint to retrieve all posts. URL: GET /api/posts
   *
   * @return List of all posts in JSON format.
   */
  @GetMapping("/list")
  public ResponseEntity<Page<PostResponse>> getPosts(
  @RequestParam(defaultValue = "0") int page,
  @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(postsService.getAllPosts(page));
  }

  /**
   * GET /api/posts/{id}
   * Fetch a single post by ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PostDetailResponse>> getPostById(@PathVariable Long id) {
    PostDetailResponse post = postsService.getPostById(id);
    return ResponseEntity.ok(ApiResponse.success("Success", post));
  }
}
