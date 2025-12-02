package com.T4.storyTracks.service;

import static com.T4.storyTracks.mapper.PostMapper.convertToDtoDetail;

import com.T4.storyTracks.dto.request.PostCreateRequest;
import com.T4.storyTracks.dto.response.PostDetailResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.exception.ResourceNotFoundException;
import com.T4.storyTracks.mapper.PostMapper;
import com.T4.storyTracks.model.Like;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.model.PostImage;
import com.T4.storyTracks.model.User;
import com.T4.storyTracks.repository.ImageRepository;
import com.T4.storyTracks.repository.LikeRepository;
import com.T4.storyTracks.repository.LikeRepository;
import com.T4.storyTracks.repository.PostRepository;
import com.T4.storyTracks.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service layer that contains business logic for handling 'posts' data. Acts as a bridge between
 * the controller and repository.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postsRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    /**
     * Constructor-based dependency injection (DI). Spring automatically injects the PostsRepository
     * bean into this service.
     */

    /**
     * Fetches all posts from the database using the repository layer.
     *
     * @return List of all posts.
     */
    public Page<PostResponse> getAllPosts(int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "rgstDtm"));
        Page<Post> postPage = postsRepository.findAll(pageable);

        // ✅ even if no posts, this still returns an empty Page
        if (postPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        List<Long> userIds = postPage.getContent().stream()
                .map(Post::getUserId)
                .distinct()
                .toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        return postPage.map(post ->
                PostMapper.convertToDto(post, userMap.get(post.getUserId()))
        );
    }

    public PostDetailResponse getPostById(Long id) {
        Post post = postsRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Posts with id: " + id + " not found"));
        return convertToDtoDetail(post);
    }

    public Page<PostResponse> getPostsByMarker(double lat, double lng, int level, int page) {
        int precision = (level == 1) ? 2 : 1;
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "rgst_dtm"));
        Page<Post> postPage = postsRepository.findPostsByCluster(lat, lng, precision, pageable);

        if (postPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        List<Long> userIds = postPage.getContent().stream()
                .map(Post::getUserId)
                .distinct()
                .toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        return postPage.map(post ->
                PostMapper.convertToDto(post, userMap.get(post.getUserId()))
        );
    }

    @Transactional
    public Long createPost(Long userId, PostCreateRequest req) {
        // 1. Save post
        Post post = Post.builder()
                .userId(userId)
                .title(req.getTitle())
                .ogText(req.getOgText())
                .aiGenText(req.getAiGenText())
                .rgstDtm(OffsetDateTime.now())
                .chngDtm(OffsetDateTime.now())
                .build();

        Post savedPost = postsRepository.save(post);

        // 2. Save images
        for (PostCreateRequest.ImageRequest img : req.getImages()) {
            PostImage postImage = PostImage.builder()
                    .post(savedPost)
                    .imgPath(img.getImgPath())
                    .imgFileName(img.getImgFileName())
                    .geoLat(img.getGeoLat())
                    .geoLong(img.getGeoLong())
                    .imgDtm(img.getImgDtm())
                    .thumbYn(img.getThumbYn() == null ? "N" : img.getThumbYn())
                    .rgstDtm(OffsetDateTime.now())
                    .build();

            imageRepository.save(postImage);
        }

        return savedPost.getPostId();
    }


    @Transactional
    public PostDetailResponse updatePostById(Long userId, Long postId, PostCreateRequest req) {

        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Posts with id: " + postId + " not found"));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unathorized : You do not own this post.");
        }

        post.setUserId(userId);
        post.setTitle(req.getTitle());
        post.setOgText(req.getOgText());
        post.setAiGenText(req.getAiGenText());
        post.setChngDtm(OffsetDateTime.now());

        imageRepository.deleteByPost_PostId(postId);

        List<PostImage> newImages = req.getImages().stream()
                .map(img -> PostImage.builder()
                        .imgFileName(img.getImgFileName())
                        .imgPath(img.getImgPath())
                        .geoLat(img.getGeoLat())
                        .geoLong(img.getGeoLong())
                        .imgDtm(img.getImgDtm())
                        .thumbYn(img.getThumbYn() == null ? "N" : img.getThumbYn())
                        .build()
                )
                .toList();

        // Remove existing images
        post.getPostImages().clear();

        // Add new images
        newImages.forEach(img -> img.setPost(post));  // attach post
        post.getPostImages().addAll(newImages);

        // Save post
        postsRepository.save(post);

        return convertToDtoDetail(post);
    }

    @Transactional
    public void deletePostById(Long postId, Long userId) {
        Post post = postsRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found or you do not have permission to delete this post."));
        postsRepository.delete(post);
    }
}
