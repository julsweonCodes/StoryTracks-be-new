package com.T4.storyTracks.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBlogHomeResponse {
    private Long id;
    private String nickname;
    private String blogName;
    private String bio;
    private String profileImg;
    private String lastLoginDtm;
    private int totalPages;
    private int currentPage;
    private List<PostResponse> posts;
}