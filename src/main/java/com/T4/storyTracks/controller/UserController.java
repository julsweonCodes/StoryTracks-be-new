package com.T4.storyTracks.controller;

import static com.T4.storyTracks.mapper.UserMapper.toUserResponse;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.request.UserLoginRequest;
import com.T4.storyTracks.dto.request.UserPwdUpdateRequest;
import com.T4.storyTracks.dto.request.UserRegisterRequest;
import com.T4.storyTracks.dto.request.UserUpdateRequest;
import com.T4.storyTracks.dto.response.ImageClusterResponse;
import com.T4.storyTracks.dto.response.LoginResponse;
import com.T4.storyTracks.dto.response.MyBlogResponse;
import com.T4.storyTracks.dto.response.UserBlogHomeResponse;
import com.T4.storyTracks.dto.response.UserResponse;
import com.T4.storyTracks.model.User;
import com.T4.storyTracks.service.JWTService;
import com.T4.storyTracks.service.PostService;
import com.T4.storyTracks.service.UserService;
import com.T4.storyTracks.mapper.UserMapper.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@RequestBody UserRegisterRequest request) {
        UserResponse newUser = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody UserLoginRequest request) {

        // 1. Validate user
        User user = userService.validateUser(request);

        // 2. Create Jwt
        String token = jwtService.generateToken(user);

        // 3. Build the response
        LoginResponse loginUser = new LoginResponse(toUserResponse(user), token);

        return ResponseEntity.ok(ApiResponse.success("User logged successfully", loginUser));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(
            @RequestHeader("Authorization") String authHeader
    ) {
        UserResponse user = userService.getUserProfile(jwtService.extractUserId(authHeader));
        return ResponseEntity.ok(ApiResponse.success("Fetched user profile successfully", user));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
            @RequestBody UserUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {
        UserResponse user = userService.updateUserProfile(jwtService.extractUserId(authHeader), request);
        return ResponseEntity.ok(ApiResponse.success("Updated user profile successfully", user));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(@PathVariable Long id, @RequestBody
    UserPwdUpdateRequest request) {
        userService.updateUserPwd(id, request);
        return ResponseEntity.ok(ApiResponse.success("Updated user password successfully", null));
    }


}
