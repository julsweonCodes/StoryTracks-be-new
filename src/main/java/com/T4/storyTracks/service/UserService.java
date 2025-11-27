package com.T4.storyTracks.service;

import com.T4.storyTracks.dto.request.UserLoginRequest;
import com.T4.storyTracks.dto.request.UserPwdUpdateRequest;
import com.T4.storyTracks.dto.request.UserRegisterRequest;
import com.T4.storyTracks.dto.request.UserUpdateRequest;
import com.T4.storyTracks.dto.response.MyBlogResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.dto.response.UserBlogHomeResponse;
import com.T4.storyTracks.dto.response.UserResponse;
import com.T4.storyTracks.mapper.PostMapper;
import com.T4.storyTracks.model.User;
import com.T4.storyTracks.repository.PostRepository;
import com.T4.storyTracks.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public UserResponse registerUser(UserRegisterRequest request) {

        // Check for duplicate user_id
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("User ID already exists");
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .userId(request.getUserId())
                .pwd(pwdEncoder.encode(request.getPwd()))
                .email(request.getEmail())
                .birthYmd(request.getBirthYmd())
                .nickname(request.getNickname())
                .blogName(request.getBlogName())
                .bio(request.getBio())
                .rgstDtm(OffsetDateTime.now())
                .chngDtm(OffsetDateTime.now())
                .build();
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse loginUser(UserLoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!pwdEncoder.matches(request.getPwd(), user.getPwd())) {
            throw new IllegalArgumentException("Wrong password");
        }

        // update last_login_dtm
        user.setLastLoginDtm(OffsetDateTime.now());
        userRepository.save(user);

        return toResponse(user);
    }

    public UserResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return toResponse(user);
    }

    public UserResponse updateUserProfile(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException());

        user.setProfileImg(request.getProfileImg());
        user.setNickname(request.getNickname());
        user.setBio(request.getBio());
        user.setChngDtm(OffsetDateTime.now());

        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    public void updateUserPwd(Long id, UserPwdUpdateRequest request ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!pwdEncoder.matches(request.getOldPassword(),  user.getPwd())) {
            throw new IllegalArgumentException("Wrong password");
        }

        if (pwdEncoder.matches(request.getNewPassword(),  user.getPwd())) {
            throw new IllegalArgumentException("Same password");
        }

        user.setPwd(pwdEncoder.encode(request.getNewPassword()));
        user.setChngDtm(OffsetDateTime.now());
        userRepository.save(user);

    }

    public UserBlogHomeResponse getUserBlogHome(Long id, int page, int size) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rgstDtm"));
        Page<PostResponse> posts = postRepository.findByUserId(id, pageable)
                .map(PostMapper::convertToDto);

        return UserBlogHomeResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .blogName(user.getBlogName())
                .bio(user.getBio())
                .profileImg(user.getProfileImg())
                .lastLoginDtm(user.getLastLoginDtm() != null ? user.getLastLoginDtm().toString() : null)
                .posts(posts.getContent())
                .totalPages(posts.getTotalPages())
                .currentPage(page)
                .build();
    }

    public MyBlogResponse getMyBlogPosts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rgstDtm"));
        Page<PostResponse> posts = postRepository.findByUserId(userId, pageable)
                .map(PostMapper::convertToDto);

        return MyBlogResponse.builder()
                .totalPages(posts.getTotalPages())
                .currentPage(page)
                .posts(posts.getContent())
                .build();
    }

    // mapper
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .email(user.getEmail())
                .birthYmd(user.getBirthYmd())
                .nickname(user.getNickname())
                .blogName(user.getBlogName())
                .bio(user.getBio())
                .profileImg(user.getProfileImg())
                .rgstDtm(user.getRgstDtm())
                .chngDtm(user.getChngDtm())
                .build();
    }

}
