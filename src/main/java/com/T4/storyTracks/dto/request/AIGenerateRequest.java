package com.T4.storyTracks.dto.request;

import com.T4.storyTracks.dto.response.ImageResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIGenerateRequest {

    private String title;
    private String ogText;
    private String aiGuide;

    private List<ImageResponse> blogImgList;
}
