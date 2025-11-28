package com.T4.storyTracks.mapper;

import com.T4.storyTracks.dto.response.ImageResponse;
import com.T4.storyTracks.dto.response.PostDetailResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.model.PostImage;
import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {
  public static PostResponse convertToDto(Post post) {
    PostImage thumb = post.getThumbImg().stream()
        .filter(img -> "Y".equals(img.getThumbYn()))
        .findFirst()
        .orElseGet(() -> post.getPostImages().isEmpty() ? null : post.getPostImages().get(0));

      return PostResponse.builder()
        .postId(post.getPostId())
        .title(post.getTitle())
        .aiGenText(post.getAiGenText())
//        .password(post.getPassword())
//        .rgstDtm(post.getRgstDtm() != null ? post.getRgstDtm().toString() : null)
//        .chngDtm(post.getChngDtm() != null ? post.getChngDtm().toString() : null)
        .rgstDtm(post.getRgstDtm())
        .chngDtm(post.getChngDtm())
        .thumbHash(thumb != null ? PostResponse.ThumbHash.builder()
            .thumbImgId(thumb.getImgId().toString())
            .thumbImgPath(thumb.getImgPath())
            .thumbGeoLat(thumb.getGeoLat())
            .thumbGeoLong(thumb.getGeoLong())
            .build() : null)
        .build();
  }

  public static PostDetailResponse convertToDtoDetail(Post post) {
    List<ImageResponse> imgList = post.getPostImages().stream()
        .map(img -> ImageResponse.builder()
            .imgId(img.getImgId())
            .postId(post.getPostId())
            .geoLat(img.getGeoLat())
            .geoLong(img.getGeoLong())
            .imgPath(img.getImgPath())
            .imgFileName(img.getImgFileName())
            .thumbYn(img.getThumbYn().equals("Y"))
            .imgDtm(img.getImgDtm() != null ? img.getImgDtm().toString() : null)
            .rgstDtm(img.getRgstDtm() != null ? img.getRgstDtm().toString() : null) // to be changed
            .filePath(img.getImgPath() != null
                ? img.getImgPath().substring(img.getImgPath().lastIndexOf('/') + 1)
                : null)
            .build()
        )
        .collect(Collectors.toList());

    return PostDetailResponse.builder()
        .postId(post.getPostId())
        .title(post.getTitle())
        .ogText(post.getOgText())
        .aiGenText(post.getAiGenText())
        .rgstDtm(post.getRgstDtm())
        .chngDtm(post.getChngDtm())
        .blogImgList(imgList)
        .build();
  }

}
