package com.T4.storyTracks.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponse {
  private Long imgId;
  private Long postId;
  private String geoLat;
  private String geoLong;
  private String imgPath;
  private String imgDtm;
  private String rgstDtm;
  private Boolean thumbYn;
  private String fileName;

}
