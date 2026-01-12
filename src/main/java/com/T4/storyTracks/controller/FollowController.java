package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.response.UserResponse;
import com.T4.storyTracks.idempotency.Idempotent;
import com.T4.storyTracks.idempotency.IdempotencyEndpoint;
import com.T4.storyTracks.model.Follower;
import com.T4.storyTracks.model.FollowerId;
import com.T4.storyTracks.repository.FollowerRepository;
import com.T4.storyTracks.service.FollowService;
import com.T4.storyTracks.service.JWTService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final JWTService jwtService;

    @PostMapping("/{userId}/follow")
    @Idempotent(endpoint = IdempotencyEndpoint.USERS_FOLLOW, pathTemplate = "/api/v1/users/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> follow(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        followService.follow(userId, authHeader);
        return ResponseEntity.ok(ApiResponse.success("Followed Succesfully", null));
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        followService.unfollow(userId, authHeader);
        return ResponseEntity.ok(ApiResponse.success("Unfollowed Succesfully", null));
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<ApiResponse<Integer>> getUserFollowerCount(
            @PathVariable Long userId) {
        int res = followService.countFollowers(userId);
        return ResponseEntity.ok(ApiResponse.success("Followers Count fetched successfully: " + res, res));
    }

    @GetMapping("/{userId}/following/count")
    public ResponseEntity<ApiResponse<Integer>> getUserFollowingCount(
            @PathVariable Long userId) {
        int res = followService.countFollowing(userId);
        return ResponseEntity.ok(ApiResponse.success("Following Count fetched successfully: " + res, res));
    }

    @GetMapping("/followers/count")
    public ResponseEntity<ApiResponse<Integer>> getFollowerCount(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtService.requireUserId(authHeader);
        int res = followService.countFollowers(userId);
        return ResponseEntity.ok(ApiResponse.success("Followers Count fetched successfully: " + res, res));
    }

    @GetMapping("/following/count")
    public ResponseEntity<ApiResponse<Integer>> getFollowingCount(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtService.requireUserId(authHeader);
        int res = followService.countFollowing(userId);
        return ResponseEntity.ok(ApiResponse.success("Following Count fetched successfully: " + res, res));
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowers(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtService.requireUserId(authHeader);
        List<UserResponse> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(ApiResponse.success("Followers fetched successfully", followers));
    }

    @GetMapping("/following")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowing(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtService.requireUserId(authHeader);
        List<UserResponse> following = followService.getFollowing(userId);
        return ResponseEntity.ok(ApiResponse.success("Following fetched successfully", following));
    }

}
