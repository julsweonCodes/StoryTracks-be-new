package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.request.UserLoginRequest;
import com.T4.storyTracks.dto.request.UserPwdUpdateRequest;
import com.T4.storyTracks.dto.request.UserRegisterRequest;
import com.T4.storyTracks.dto.request.UserUpdateRequest;
import com.T4.storyTracks.dto.response.ImageClusterResponse;
import com.T4.storyTracks.dto.response.MyBlogResponse;
import com.T4.storyTracks.dto.response.UserBlogHomeResponse;
import com.T4.storyTracks.dto.response.UserResponse;
import com.T4.storyTracks.service.PostService;
import com.T4.storyTracks.service.UserService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
//@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@RequestBody UserRegisterRequest request) {
        UserResponse newUser = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody UserLoginRequest request) {
        UserResponse newUser = userService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success("User logged successfully", newUser));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@PathVariable Long id) {
        UserResponse user = userService.getUserProfile(id);
        return ResponseEntity.ok(ApiResponse.success("Fetched user profile successfully", user));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(@PathVariable Long id, @RequestBody
            UserUpdateRequest request) {
        UserResponse user = userService.updateUserProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("Updated user profile successfully", user));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(@PathVariable Long id, @RequestBody
    UserPwdUpdateRequest request) {
        userService.updateUserPwd(id, request);
        return ResponseEntity.ok(ApiResponse.success("Updated user password successfully", null));
    }


}
