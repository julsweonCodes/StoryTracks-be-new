package com.T4.storyTracks.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used when the logged-in user is accessing their own blog.
 * Contains only paginated posts because user info is already in session.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyBlogResponse {
    private int totalPages;
    private int currentPage;
    private List<PostResponse> posts;
}