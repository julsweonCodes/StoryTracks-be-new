package com.T4.storyTracks.service;

import com.T4.storyTracks.dto.response.MyBlogResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.dto.response.UserBlogHomeResponse;
import com.T4.storyTracks.mapper.PostMapper;
import com.T4.storyTracks.model.FollowerId;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.model.User;
import com.T4.storyTracks.repository.FollowerRepository;
import com.T4.storyTracks.repository.LikeRepository;
import com.T4.storyTracks.repository.PostRepository;
import com.T4.storyTracks.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBlogService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final FollowerRepository followerRepository;

    public UserBlogHomeResponse getUserBlogHome(Long id, int page, int size) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rgstDtm"));
        Page<PostResponse> posts = postRepository.findByUserId(id, pageable)
                .map(post -> PostMapper.convertToDto(post, user));

        return UserBlogHomeResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .blogName(user.getBlogName())
                .bio(user.getBio())
                .profileImg(user.getProfileImg())
                .lastLoginDtm(
                        user.getLastLoginDtm() != null ? user.getLastLoginDtm().toString() : null)
                .posts(posts.getContent())
                .totalPages(posts.getTotalPages())
                .currentPage(page)
                .build();
    }

    public MyBlogResponse getMyBlogPosts(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with id: " + userId));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rgstDtm"));
        Page<PostResponse> posts = postRepository.findByUserId(userId, pageable)
                .map(post -> PostMapper.convertToDto(post, user));

        return MyBlogResponse.builder()
                .totalPages(posts.getTotalPages())
                .currentPage(page)
                .posts(posts.getContent())
                .build();
    }

    public List<PostResponse> getPostsByMarker(Long ownerId, double lat, double lng, int level, Long viewerId) {
        int precision = (level == 1) ? 2 : 1;

        // 1. fetch user info
        User blogOwner = userRepository.findById(ownerId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + ownerId));

        // 2. fetch user posts
        List<Post> posts = postRepository.findPostsByMarkerClusterNoPage(ownerId, lat, lng, precision);
        if (posts.isEmpty()) return List.of();

        // 3. map to List<PostResponse>
        return posts.stream()
                .map(post -> {
                    PostResponse dto = PostMapper.convertToDto(post, blogOwner);
                    dto.setIsLiked(
                            viewerId != null && likeRepository.existsByPostIdAndUserId(
                                    post.getPostId(), viewerId)
                    );

                    dto.setIsFollowed(
                            viewerId != null && followerRepository.existsById(
                                    new FollowerId(ownerId, viewerId))
                    );
                    return dto;
                }).toList();
    }
}
