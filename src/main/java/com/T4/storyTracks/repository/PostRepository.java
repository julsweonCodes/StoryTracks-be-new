package com.T4.storyTracks.repository;

import com.T4.storyTracks.model.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    //SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.images WHERE p.post_id = ?
    // Prevents N+1 queries
    // ✅ 게시글 상세 조회 (postImages까지 fetch)
    @EntityGraph(attributePaths = "postImages")
    Optional<Post> findByPostId(Long postId);

    Page<Post> findByUserId(Long userId, Pageable pageable);

    List<Post> findByUserIdOrderByRgstDtmDesc(Long userId);

    Optional<Post> findByPostIdAndUserId(Long postId, Long userId);

    @Query(value = """
        SELECT DISTINCT p.post_id, p.user_id, p.title, p.og_text, p.ai_gen_text,
               p.password, p.rgst_dtm, p.chng_dtm
        FROM posts p
        JOIN images i ON p.post_id = i.post_id
        WHERE i.thumb_yn = 'Y'
          AND ROUND(i.geo_lat::NUMERIC, :precision) = ROUND(CAST(:lat AS NUMERIC), :precision)
          AND ROUND(i.geo_long::NUMERIC, :precision) = ROUND(CAST(:lng AS NUMERIC), :precision)
        ORDER BY p.rgst_dtm DESC
        """,

            countQuery = """
        SELECT COUNT(DISTINCT p.post_id)
        FROM posts p
        JOIN images i ON p.post_id = i.post_id
        WHERE i.thumb_yn = 'Y'
          AND ROUND(i.geo_lat::NUMERIC, :precision) = ROUND(CAST(:lat AS NUMERIC), :precision)
          AND ROUND(i.geo_long::NUMERIC, :precision) = ROUND(CAST(:lng AS NUMERIC), :precision)
        """,
            nativeQuery = true)
    Page<Post> findPostsByCluster(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("precision") int precision,
            Pageable pageable
    );

    @Query(value = """
    SELECT p.*
    FROM posts p
    JOIN images i ON p.post_id = i.post_id
    WHERE p.user_id = :blogOwnerId
      AND ROUND(i.geo_lat::NUMERIC, :precision) = ROUND(CAST(:lat AS NUMERIC), :precision)
      AND ROUND(i.geo_long::NUMERIC, :precision) = ROUND(CAST(:lng AS NUMERIC), :precision)
    ORDER BY p.rgst_dtm DESC
""", nativeQuery = true)
    List<Post> findPostsByMarkerClusterNoPage(
            @Param("blogOwnerId") Long blogOwnerId,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("precision") int precision
    );

}
