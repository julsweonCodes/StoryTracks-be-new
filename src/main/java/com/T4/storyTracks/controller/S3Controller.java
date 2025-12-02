package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.service.JWTService;
import com.T4.storyTracks.service.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;
    private final JWTService jwtService;

    /**
     * ✅ Upload file to S3 and return the public URL.
     * POST /api/s3/upload/profile
     */
    @PostMapping("/upload/profile")
    public ResponseEntity<ApiResponse<String>> uploadProfile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader);
            String url = s3Service.uploadProfileImg(file);
            return ResponseEntity.ok(ApiResponse.success("Success", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("500", "Profile upload failed: " + e.getMessage()));
        }
    }

    /**
     * ✅ Multi upload — for post images (up to 10)
     */
    @PostMapping("/upload/blog-images")
    public ResponseEntity<ApiResponse<List<String>>> uploadPostImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader);
            List<String> urls = s3Service.uploadPostFiles(files);
            return ResponseEntity.ok(ApiResponse.success("Success", urls));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("500", "Post image upload failed: " + e.getMessage()));
        }
    }
}
