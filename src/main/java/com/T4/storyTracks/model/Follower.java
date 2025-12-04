package com.T4.storyTracks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "followers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follower {

    @EmbeddedId
    private FollowerId followerId;

    @Column(name = "followed_at")
    private OffsetDateTime followedAt;

}
