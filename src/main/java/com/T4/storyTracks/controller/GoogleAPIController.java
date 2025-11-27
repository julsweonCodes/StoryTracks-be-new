package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.response.ImageClusterResponse;
import com.T4.storyTracks.service.GoogleAPIService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/google")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class GoogleAPIController {

    private final GoogleAPIService googleAPIService;

    @GetMapping("/clusters")
    public ResponseEntity<ApiResponse<List<ImageClusterResponse>>> getImageClusters() {
        List<ImageClusterResponse> clusters = googleAPIService.getAllClusters();
        return ResponseEntity.ok(ApiResponse.success("Fetched Google Map clusters successfully", clusters));
    }

    @GetMapping("/clusters/recompute")
    public ResponseEntity<ApiResponse<List<ImageClusterResponse>>> recomputeClusters() {
        googleAPIService.computeImageClusters();
        List<ImageClusterResponse> clusters = googleAPIService.getAllClusters();
        return ResponseEntity.ok(ApiResponse.success("Cluster recomputation triggered manually", clusters));
    }

    @GetMapping("/user-blog/{userId}/image-markers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserMarkers(
            @PathVariable Long userId) {

        List<Map<String, Object>> clusters = googleAPIService.getUserClusters(userId);

        return ResponseEntity.ok(ApiResponse.success(
                "Fetched user image markers successfully",
                clusters
        ));
    }
}
