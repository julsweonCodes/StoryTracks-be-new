package com.T4.storyTracks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageClusterResponse {
    private Double clusterLat;
    private Double clusterLong;
    private Integer imageCount;
    private String thumbImgPath;
    private Integer clusterLevel;
}
