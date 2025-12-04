package com.T4.storyTracks.repository;

import com.T4.storyTracks.model.Follower;
import com.T4.storyTracks.model.FollowerId;
import com.T4.storyTracks.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    // 1. Followers count
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.id.followId = :userId")
    int countFollowers(@Param("userId") Long userId);

    // 2. Following count
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.id.followedById = :userId")
    int countFollowing(@Param("userId") Long userId);

    // 3. Followers list
    @Query("""
        SELECT u
        FROM Follower f
        JOIN User u ON f.id.followedById = u.id
        WHERE f.id.followId = :userId
    """)
    List<User> getFollowers(@Param("userId") Long userId);

    // 4. Following list
    @Query("""
        SELECT u
        FROM Follower f
        JOIN User u ON f.id.followId = u.id
        WHERE f.id.followedById = :userId
    """)
    List<User> getFollowing(@Param("userId") Long userId);
}
