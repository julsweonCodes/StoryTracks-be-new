package com.T4.storyTracks.repository;

import com.T4.storyTracks.model.ImageCluster;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageClusterRepository extends JpaRepository<ImageCluster, Long> {

    // For main feed (global)
    List<ImageCluster> findByClusterLevelOrderByImageCountDesc(int clusterLevel);

    // For main feed with limit
    @Query(value = "SELECT * FROM image_clusters WHERE cluster_level = :level ORDER BY image_count DESC LIMIT :limit", nativeQuery = true)
    List<ImageCluster> findTopClusters(@Param("level") int level, @Param("limit") int limit);
}