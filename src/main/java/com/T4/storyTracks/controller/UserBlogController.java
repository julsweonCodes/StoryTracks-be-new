package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.response.MyBlogResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.dto.response.UserBlogHomeResponse;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.model.User;
import com.T4.storyTracks.service.JWTService;
import com.T4.storyTracks.service.UserBlogService;
import com.T4.storyTracks.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-blog")
@RequiredArgsConstructor
public class UserBlogController {
    private final UserBlogService userBlogService;
    private final JWTService jwtService;

    @GetMapping("/{id}/blog-home")
    public ResponseEntity<ApiResponse<UserBlogHomeResponse>> getUserBlogHome(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UserBlogHomeResponse blogHome = userBlogService.getUserBlogHome(id, page, size);
        return ResponseEntity.ok(ApiResponse.success("Fetched user blog home successfully", blogHome));
    }

    @GetMapping("/{id}/my-blog-home")
    public ResponseEntity<ApiResponse<MyBlogResponse>> getMyBlogHome(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        MyBlogResponse response = userBlogService.getMyBlogPosts(id, page, size);
        return ResponseEntity.ok(ApiResponse.success("Fetched my blog posts successfully", response));
    }

    /**
     * GET : get post under the map marker
     */
    @GetMapping("/marker-my")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getMyMarkerPosts(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int level
    ) {
        Long userId = jwtService.requireUserId(authHeader);

        List<PostResponse> res = userBlogService.getPostsByMarker(userId, lat, lng, level, userId);

        return ResponseEntity.ok(ApiResponse.success("Fetched user posts by marker successfully (my blog)", res));
    }

    @GetMapping("/{userId}/marker")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getUserMarkerPosts(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int level
    ) {
        Long viewerId = jwtService.extractUserIdOrNull(authHeader);
        List<PostResponse> res = userBlogService.getPostsByMarker(userId, lat, lng, level, viewerId);
        return ResponseEntity.ok(ApiResponse.success("Fetched user posts by marker successfully", res));
    }

}
