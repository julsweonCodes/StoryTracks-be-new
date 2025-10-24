package com.T4.storyTracks.service;

import com.T4.storyTracks.exception.ResourceNotFoundException;
import com.T4.storyTracks.model.Posts;
import com.T4.storyTracks.repository.PostsRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service layer that contains business logic for handling 'posts' data.
 * Acts as a bridge between the controller and repository.
 */
@Service
public class PostsService {
  private final PostsRepository postsRepository;

  /**
   * Constructor-based dependency injection (DI).
   * Spring automatically injects the PostsRepository bean into this service.
   */
  public PostsService(PostsRepository postsRepository) {
    this.postsRepository = postsRepository;
  }


  /**
   * Fetches all posts from the database using the repository layer.
   * @return List of all posts.
   */
  public List<Posts> getAllPosts() {
    return postsRepository.findAll();
  }

  public Posts getPostById(Long id) {
    return postsRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Posts with id: " + id + " not found"));
  }

}
