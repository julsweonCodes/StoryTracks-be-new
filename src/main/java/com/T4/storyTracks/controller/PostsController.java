package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.model.Posts;
import com.T4.storyTracks.service.PostsService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts") // Base URL Path for this controller
@CrossOrigin(origins = "*") // Allows requests from all origins (for frontend integration).
public class PostsController {

  private final PostsService postsService;

  /**
   * Constructor-based dependency injection (DI). Spring injects an instance of PostsService
   * automatically.
   */
  public PostsController(PostsService postsService) {
    this.postsService = postsService;
  }

  /**
   * GET endpoint to retrieve all posts. URL: GET /api/posts
   *
   * @return List of all posts in JSON format.
   */
  @GetMapping
  public List<Posts> findAll() {
    return postsService.getAllPosts();
  }

  /**
   * GET /api/posts/{id}
   * Fetch a single post by ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Posts>> getPostById(@PathVariable Long id) {
    Posts post = postsService.getPostById(id);
    return ResponseEntity.ok(ApiResponse.success("Success", post));
  }
}
