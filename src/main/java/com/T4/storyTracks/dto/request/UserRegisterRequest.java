package com.T4.storyTracks.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    private String userId;
    private String pwd;
    private String email;
    private String nickname;
    private String blogName;
    private String bio;
    private String birthYmd;
}
