package com.T4.storyTracks.service;

import static com.T4.storyTracks.mapper.PostMapper.convertToDtoDetail;

import com.T4.storyTracks.dto.request.PostCreateRequest;
import com.T4.storyTracks.dto.response.PostDetailResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.exception.ResourceNotFoundException;
import com.T4.storyTracks.mapper.PostMapper;
import com.T4.storyTracks.model.FollowerId;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.model.PostImage;
import com.T4.storyTracks.model.User;
import com.T4.storyTracks.repository.FollowerRepository;
import com.T4.storyTracks.repository.ImageRepository;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postsRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final FollowerRepository followerRepository;
    private final GoogleAPIService googleAPIService;

    // ==========================================================
    // 1. GET ALL POSTS (FEED) – with isLiked
    // ==========================================================
    public Page<PostResponse> getAllPosts(int page, Long viewerId) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by("rgstDtm").descending());
        Page<Post> postPage = postsRepository.findAll(pageable);

        if (postPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // Get all userIds for mapping
        List<Long> userIds = postPage.getContent().stream()
                .map(Post::getUserId)
                .distinct()
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return postPage.map(post -> {
            PostResponse dto = PostMapper.convertToDto(post, userMap.get(post.getUserId()));

            boolean liked = false;
            if (viewerId != null) {
                liked = likeRepository.existsByPostIdAndUserId(post.getPostId(), viewerId);
            }
            dto.setIsLiked(liked);

            boolean following = false;
            if (viewerId != null) {
                following = followerRepository.existsById(new FollowerId(post.getUserId(), viewerId));
            }
            dto.setIsFollowed(following);
            return dto;
        });
    }

    // ==========================================================
    // 2. GET POSTS BY MARKER (MAP FEED) – with isLiked
    // ==========================================================
    public Page<PostResponse> getPostsByMarker(double lat, double lng, int level, int page, Long viewerId) {

        int precision = (level == 1) ? 2 : 1;

        Pageable pageable = PageRequest.of(page, 20, Sort.by("rgst_dtm").descending());
        Page<Post> postPage = postsRepository.findPostsByCluster(lat, lng, precision, pageable);

        if (postPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Long> userIds = postPage.getContent().stream()
                .map(Post::getUserId)
                .distinct()
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return postPage.map(post -> {
            PostResponse dto = PostMapper.convertToDto(post, userMap.get(post.getUserId()));

            boolean liked = false;
            if (viewerId != null) {
                liked = likeRepository.existsByPostIdAndUserId(post.getPostId(), viewerId);
            }
            dto.setIsLiked(liked);

            boolean following = false;
            if (viewerId != null) {
                following = followerRepository.existsById(new FollowerId(post.getUserId(), viewerId));
            }
            dto.setIsFollowed(following);

            return dto;
        });
    }

    // ==========================================================
    // 3. GET POST DETAIL BY ID – with isLiked (Corrected!)
    // ==========================================================
    public PostDetailResponse getPostById(Long postId, Long viewerId) {

        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        PostDetailResponse res = convertToDtoDetail(post);

        boolean liked = false;
        if (viewerId != null) {
            liked = likeRepository.existsByPostIdAndUserId(postId, viewerId);
        }
        res.setIsLiked(liked);

        boolean following = false;
        if (viewerId != null) {
            following = followerRepository.existsById(new FollowerId(post.getUserId(), viewerId));
        }
        res.setIsFollowed(following);

        return res;
    }

    // ==========================================================
    // 4. CREATE POST
    // ==========================================================
    @Transactional
    public Long createPost(Long userId, PostCreateRequest req) {

        Post post = Post.builder()
                .userId(userId)
                .title(req.getTitle())
                .ogText(req.getOgText())
                .aiGenText(req.getAiGenText())
                .rgstDtm(OffsetDateTime.now())
                .chngDtm(OffsetDateTime.now())
                .build();

        Post savedPost = postsRepository.save(post);

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

        googleAPIService.clearUserCache(userId);
        return savedPost.getPostId();
    }

    // ==========================================================
    // 5. UPDATE POST (owner-only)
    // ==========================================================
    @Transactional
    public PostDetailResponse updatePostById(Long userId, Long postId, PostCreateRequest req) {

        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: You do not own this post.");
        }

        post.setTitle(req.getTitle());
        post.setOgText(req.getOgText());
        post.setAiGenText(req.getAiGenText());
        post.setChngDtm(OffsetDateTime.now());

        imageRepository.deleteByPost_PostId(postId);
        post.getPostImages().clear();

        List<PostImage> newImages = req.getImages().stream()
                .map(img -> PostImage.builder()
                        .post(post)
                        .imgFileName(img.getImgFileName())
                        .imgPath(img.getImgPath())
                        .geoLat(img.getGeoLat())
                        .geoLong(img.getGeoLong())
                        .imgDtm(img.getImgDtm())
                        .thumbYn(img.getThumbYn() == null ? "N" : img.getThumbYn())
                        .rgstDtm(OffsetDateTime.now())
                        .build())
                .toList();

        post.getPostImages().addAll(newImages);

        postsRepository.save(post);

        PostDetailResponse res = convertToDtoDetail(post);
        boolean liked = false;
        if (userId != null) {
            liked = likeRepository.existsByPostIdAndUserId(postId, userId);;
        }
        res.setIsLiked(liked);

        return res;
    }

    // ==========================================================
    // 6. DELETE POST (owner-only)
    // ==========================================================
    @Transactional
    public void deletePostById(Long postId, Long userId) {

        Post post = postsRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Post not found or you do not own this post"));

        postsRepository.delete(post);
    }
}
