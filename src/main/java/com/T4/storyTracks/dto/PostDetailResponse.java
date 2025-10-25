package com.T4.storyTracks.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class PostDetailResponse {
  private Long postId;
  private String title;
  private String aiGenText;
//  private String password;
  private String rgstDtm;
  private String chngDtm;
  private List<ImageResponse> blogImgList; // 상세 조회 시 전체 이미지 리스트
}
