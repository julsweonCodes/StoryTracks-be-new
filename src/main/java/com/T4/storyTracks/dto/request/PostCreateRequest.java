package com.T4.storyTracks.dto.request;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {

//    private Long userId;  // frontend must include it -> to JWT
    private String title;
    private String ogText;
    private String aiGenText;
    private List<ImageRequest> images;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ImageRequest {
        private String imgFileName;
        private String imgPath;
        private String geoLat;
        private String geoLong;
        private OffsetDateTime imgDtm;
        private String thumbYn; // Y or N
    }
}

