package com.T4.storyTracks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerId implements Serializable {
    @Column(name="follow_id")
    private Long followId;

    @Column(name="followed_by_id")
    private Long followedById;
}
