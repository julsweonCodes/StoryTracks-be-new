package com.T4.storyTracks.mapper;

import com.T4.storyTracks.dto.ImageResponse;
import com.T4.storyTracks.dto.PostDetailResponse;
import com.T4.storyTracks.dto.PostResponse;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.model.PostImage;
import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {
  public static PostResponse convertToDto(Post post) {
    PostImage thumb = post.getThumbImg().stream()
        .filter(img -> "Y".equals(img.getThumbYn()))
        .findFirst()
        .orElse(null);

    return PostResponse.builder()
        .postId(post.getPostId())
        .title(post.getTitle())
        .aiGenText(post.getAiGenText())
//        .password(post.getPassword())
        .rgstDtm(post.getRgstDtm() != null ? post.getRgstDtm().toString() : null)
        .chngDtm(post.getChngDtm() != null ? post.getChngDtm().toString() : null)
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
            .imgDtm(img.getImgDtm() != null ? img.getImgDtm().toString() : null)
            .rgstDtm(img.getRgstDtm() != null ? img.getRgstDtm().toString() : null)
            .fileName(img.getImgPath() != null
                ? img.getImgPath().substring(img.getImgPath().lastIndexOf('/') + 1)
                : null)
            .build()
        )
        .collect(Collectors.toList());

    return PostDetailResponse.builder()
        .postId(post.getPostId())
        .title(post.getTitle())
        .aiGenText(post.getAiGenText())
        .rgstDtm(post.getRgstDtm() != null ? post.getRgstDtm().toString() : null)
        .chngDtm(post.getChngDtm() != null ? post.getChngDtm().toString() : null)
        .blogImgList(imgList)
        .build();
  }

}
