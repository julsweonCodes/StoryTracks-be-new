package com.T4.storyTracks.repository;

import com.T4.storyTracks.model.Follower;
import com.T4.storyTracks.model.FollowerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {

}
