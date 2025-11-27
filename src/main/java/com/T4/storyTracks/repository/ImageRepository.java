package com.T4.storyTracks.repository;

import com.T4.storyTracks.model.PostImage;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<PostImage, Long> {

    @Query(value = """
            SELECT 
                ROUND(i.geo_lat::NUMERIC, 2) AS cluster_lat,
                ROUND(i.geo_long::NUMERIC, 2) AS cluster_long,
                COUNT(*) AS image_count,
                MIN(i.img_path) AS thumb_img_path
            FROM images i
            JOIN posts p ON i.post_id = p.post_id
            WHERE p.user_id = :userId AND i.thumb_yn = 'Y'
            GROUP BY ROUND(i.geo_lat::NUMERIC, 2), ROUND(i.geo_long::NUMERIC, 2)
            """, nativeQuery = true)
    List<Map<String, Object>> findUserImageClusters(@Param("userId") Long userId);

}
