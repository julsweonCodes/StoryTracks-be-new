package com.T4.storyTracks.mapper;

import com.T4.storyTracks.dto.response.UserResponse;
import com.T4.storyTracks.model.User;

public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .blogName(user.getBlogName())
                .bio(user.getBio())
                .birthYmd(user.getBirthYmd())
                .profileImg(user.getProfileImg())
                .rgstDtm(user.getRgstDtm())
                .chngDtm(user.getChngDtm())
                .build();
    }
}
