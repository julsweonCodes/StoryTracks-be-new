package com.T4.storyTracks.dto.response;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String userId;
    private String nickname;
    private String email;
    private String blogName;
    private String bio;
    private String birthYmd;
    private String profileImg;
    private OffsetDateTime rgstDtm;
    private OffsetDateTime chngDtm;

}
