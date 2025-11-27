package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.response.MyBlogResponse;
import com.T4.storyTracks.dto.response.UserBlogHomeResponse;
import com.T4.storyTracks.model.User;
import com.T4.storyTracks.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-blog")
public class UserBlogController {
    private UserService userService;

    public UserBlogController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/blog-home")
    public ResponseEntity<ApiResponse<UserBlogHomeResponse>> getUserBlogHome(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UserBlogHomeResponse blogHome = userService.getUserBlogHome(id, page, size);
        return ResponseEntity.ok(ApiResponse.success("Fetched user blog home successfully", blogHome));
    }

    @GetMapping("/{id}/my-blog-home")
    public ResponseEntity<ApiResponse<MyBlogResponse>> getMyBlogHome(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        MyBlogResponse response = userService.getMyBlogPosts(id, page, size);
        return ResponseEntity.ok(ApiResponse.success("Fetched my blog posts successfully", response));
    }

}
