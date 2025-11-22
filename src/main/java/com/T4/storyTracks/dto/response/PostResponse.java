package com.T4.storyTracks.dto.response;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
  private Long postId;
  private String title;
  private String ogText;
  private String aiGenText;
//  private String password;
  private OffsetDateTime rgstDtm;
  private OffsetDateTime chngDtm;

  private ThumbHash thumbHash;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ThumbHash {
    private String thumbImgId;
    private String thumbImgPath;
    private String thumbGeoLat;
    private String thumbGeoLong;
  }
}
