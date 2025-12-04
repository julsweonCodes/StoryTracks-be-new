package com.T4.storyTracks.service;

import com.T4.storyTracks.model.Follower;
import com.T4.storyTracks.model.FollowerId;
import com.T4.storyTracks.repository.FollowerRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowerRepository followerRepository;
    private final JWTService jwtService;

    public void follow(Long targetUserId, String authHeader) {
        Long currUserId = jwtService.requireUserId(authHeader);

        // this will be handled in the frontend (not showing the follow button)
        if (currUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        FollowerId id = new FollowerId(targetUserId, currUserId);

        if (followerRepository.existsById(id)) {
            return; // already following
        }

        followerRepository.save(Follower.builder()
                .followerId(id)
                .followedAt(OffsetDateTime.now())
                .build());
    }

    public void unfollow(Long targetUserId, String authHeader) {
        Long currUserId = jwtService.requireUserId(authHeader);

        FollowerId id = new FollowerId(targetUserId, currUserId);
        if (followerRepository.existsById(id)) {
            followerRepository.deleteById(id);
        }
    }

}
