package com.T4.storyTracks.controller;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.request.AIGenerateRequest;
import com.T4.storyTracks.dto.response.AiGenerateResponse;
import com.T4.storyTracks.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<AiGenerateResponse>> generateAI(
            @RequestBody AIGenerateRequest req
    ) {
        AiGenerateResponse response = geminiService.generate(req);
        return ResponseEntity.ok(ApiResponse.success("AI blog text generated", response));
    }

}
