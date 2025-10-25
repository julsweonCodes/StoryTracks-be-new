package com.T4.storyTracks.service;

import static com.T4.storyTracks.mapper.PostMapper.convertToDtoDetail;

import com.T4.storyTracks.dto.ImageResponse;
import com.T4.storyTracks.dto.PostDetailResponse;
import com.T4.storyTracks.dto.PostResponse;
import com.T4.storyTracks.exception.ResourceNotFoundException;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.model.PostImage;
import com.T4.storyTracks.repository.PostRepository;
import com.T4.storyTracks.mapper.PostMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

/**
 * Service layer that contains business logic for handling 'posts' data.
 * Acts as a bridge between the controller and repository.
 */
@Service
public class PostService {
  private final PostRepository postsRepository;

  /**
   * Constructor-based dependency injection (DI).
   * Spring automatically injects the PostsRepository bean into this service.
   */
  public PostService(PostRepository postsRepository) {
    this.postsRepository = postsRepository;
  }

  /**
   * Fetches all posts from the database using the repository layer.
   * @return List of all posts.
   */
  public Page<PostResponse> getAllPosts(int page) {
    Pageable pageable = (Pageable) PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "rgstDtm"));

    return postsRepository.findAll(pageable)
        .map(PostMapper::convertToDto);
  }

  public PostDetailResponse getPostById(Long id) {
    Post post = postsRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Posts with id: " + id + " not found"));
    return convertToDtoDetail(post);
  }
}
