package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.model.FollowerId;
import com.T4.storyTracks.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> follow(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        followService.follow(userId, authHeader);
        return ResponseEntity.ok(ApiResponse.success("Followed Succesfully", null));
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        followService.unfollow(userId, authHeader);
        return ResponseEntity.ok(ApiResponse.success("Unfollowed Succesfully", null));
    }

}
