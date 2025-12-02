package com.T4.storyTracks.mapper;

import com.T4.storyTracks.dto.response.ImageResponse;
import com.T4.storyTracks.dto.response.PostDetailResponse;
import com.T4.storyTracks.dto.response.PostResponse;
import com.T4.storyTracks.model.Post;
import com.T4.storyTracks.model.PostImage;
import com.T4.storyTracks.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    // ==========================================================
    // 1. LIST ITEM (PostResponse)
    // ==========================================================
    public static PostResponse convertToDto(Post post, User user) {

        PostImage thumb = selectThumbnail(post);

        return PostResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .ogText(post.getOgText())
                .aiGenText(post.getAiGenText())
                .rgstDtm(post.getRgstDtm())
                .chngDtm(post.getChngDtm())

                // author info
                .userId(user.getId())
                .nickname(user.getNickname())
                .blogName(user.getBlogName())
                .profileImg(user.getProfileImg())

                // thumbnail
                .thumbHash(thumb != null ? PostResponse.ThumbHash.builder()
                        .thumbImgId(String.valueOf(thumb.getImgId()))
                        .thumbImgPath(thumb.getImgPath())
                        .thumbGeoLat(thumb.getGeoLat())
                        .thumbGeoLong(thumb.getGeoLong())
                        .build()
                        : null)

                // isLiked set in PostService, not here
                .build();
    }

    // ==========================================================
    // 2. DETAIL ITEM (PostDetailResponse)
    // ==========================================================
    public static PostDetailResponse convertToDtoDetail(Post post) {

        List<ImageResponse> imgList = post.getPostImages().stream()
                .map(img -> ImageResponse.builder()
                        .imgId(img.getImgId())
                        .postId(post.getPostId())
                        .geoLat(img.getGeoLat())
                        .geoLong(img.getGeoLong())
                        .imgPath(img.getImgPath())
                        .imgFileName(img.getImgFileName())
                        .thumbYn("Y".equals(img.getThumbYn()))
                        .imgDtm(img.getImgDtm() != null ? img.getImgDtm().toString() : null)
                        .rgstDtm(img.getRgstDtm() != null ? img.getRgstDtm().toString() : null)
                        .filePath(
                                img.getImgPath() != null && img.getImgPath().contains("/")
                                        ? img.getImgPath()
                                        .substring(img.getImgPath().lastIndexOf('/') + 1)
                                        : img.getImgFileName()
                        )
                        .build()
                )
                .collect(Collectors.toList());

        return PostDetailResponse.builder()
                .userId(post.getUserId())
                .postId(post.getPostId())
                .title(post.getTitle())
                .ogText(post.getOgText())
                .aiGenText(post.getAiGenText())
                .rgstDtm(post.getRgstDtm())
                .chngDtm(post.getChngDtm())
                .blogImgList(imgList)
                // isLiked set in PostService, not here
                .build();
    }

    // ==========================================================
    // 3. Thumbnail Selector (safe)
    // ==========================================================
    private static PostImage selectThumbnail(Post post) {

        // 1) Use explicit thumbnail if exists
        return post.getThumbImg().stream()
                .filter(img -> "Y".equals(img.getThumbYn()))
                .findFirst()

                // 2) Otherwise use first post image if exists
                .or(() -> post.getPostImages().stream().findFirst())

                // 3) Otherwise no thumbnail
                .orElse(null);
    }
}
