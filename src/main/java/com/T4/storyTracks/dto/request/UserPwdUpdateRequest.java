package com.T4.storyTracks.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPwdUpdateRequest {
    private String oldPassword;
    private String newPassword;

}
