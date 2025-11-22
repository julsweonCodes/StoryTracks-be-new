package com.T4.storyTracks.service;

import com.T4.storyTracks.dto.request.UserLoginRequest;
import com.T4.storyTracks.dto.request.UserPwdUpdateRequest;
import com.T4.storyTracks.dto.request.UserRegisterRequest;
import com.T4.storyTracks.dto.request.UserUpdateRequest;
import com.T4.storyTracks.dto.response.UserResponse;
import com.T4.storyTracks.model.User;
import com.T4.storyTracks.repository.UserRepository;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
