package com.T4.storyTracks.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long userId;
    private Long postId;
    private String title;
    private String ogText;
    private String aiGenText;
    //  private String password;
    private OffsetDateTime rgstDtm;
    private OffsetDateTime chngDtm;
    private List<ImageResponse> blogImgList; // 상세 조회 시 전체 이미지 리스트
}
