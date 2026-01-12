package com.T4.storyTracks.service;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.request.AIGenerateRequest;
import com.T4.storyTracks.dto.response.AiGenerateResponse;
import com.T4.storyTracks.dto.response.ImageClusterResponse;
import com.T4.storyTracks.model.ImageCluster;
import com.T4.storyTracks.repository.ImageClusterRepository;
import com.T4.storyTracks.repository.ImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAPIService {

    private final JdbcTemplate jdbcTemplate;
    private final ImageClusterRepository imageClusterRepository;
    private final ImageRepository imageRepository;

    /**
     * 🔁 Runs once a day at midnight to recompute image clusters for Google Maps.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void computeImageClustersIncremental() {
        log.info("🛰️ Starting image cluster recomputation (incremental)...");

        // 1) 마지막 성공 실행 시각 조회 (없으면 24시간 전 등 기본값)
        LocalDateTime lastSuccessAt = jdbcTemplate.query(
                "SELECT last_success_at FROM job_checkpoint WHERE job_name = ?",
                rs -> rs.next() ? rs.getTimestamp(1).toLocalDateTime() : null,
                "image_cluster_recompute");

        if (lastSuccessAt == null) {
            lastSuccessAt = LocalDateTime.now().minusDays(1);
            jdbcTemplate.update("""
                        INSERT INTO job_checkpoint(job_name, last_success_at)
                        VALUES (?, ?)
                        ON CONFLICT (job_name) DO NOTHING
                    """, "image_cluster_recompute", Timestamp.valueOf(lastSuccessAt));
        }

        // 2) 변경된 게시글(또는 이미지) 기반으로 영향받는 userId 목록 추출
        List<Long> changedUserIds = jdbcTemplate.queryForList("""
                    SELECT DISTINCT p.user_id
                    FROM posts p
                    WHERE (p.updated_at >= ? OR (p.deleted_at IS NOT NULL AND p.deleted_at >= ?))
                """, Long.class, Timestamp.valueOf(lastSuccessAt), Timestamp.valueOf(lastSuccessAt));

        if (changedUserIds.isEmpty()) {
            log.info("✅ No changes detected. Skip recomputation.");
            jdbcTemplate.update("""
                        UPDATE job_checkpoint
                        SET last_success_at = ?
                        WHERE job_name = ?
                    """, Timestamp.valueOf(LocalDateTime.now()), "image_cluster_recompute");
            return;
        }

        log.info("🔁 Recomputing clusters for {} users", changedUserIds.size());

        // 3) IN 절 파라미터 바인딩을 위해 placeholder 생성
        String inClause = changedUserIds.stream().map(id -> "?").collect(Collectors.joining(","));
        Object[] params = changedUserIds.stream().map(Long::valueOf).toArray();

        // 4) 영향받는 userId에 대해서만 기존 집계 데이터 삭제 후 재삽입 (부분 갱신)
        // * 전체 truncate 금지
        jdbcTemplate.update("DELETE FROM image_clusters WHERE user_id IN (" + inClause + ")", params);

        // 5) Level 별 집계 후 INSERT (pre-aggregation)
        // image_clusters 테이블에 user_id 컬럼이 있어야 이 설계가 성립함
        // (없다면 user_id 기준이 아니라 blog_id 기준으로 바꾸면 됨)
        String insertLevel1 = """
                    INSERT INTO image_clusters (user_id, cluster_level, cluster_lat, cluster_long, image_count, thumb_img_path, updated_at)
                    SELECT
                        i.user_id,
                        1 AS cluster_level,
                        ROUND(i.geo_lat::NUMERIC, 2) AS cluster_lat,
                        ROUND(i.geo_long::NUMERIC, 2) AS cluster_long,
                        COUNT(*) AS image_count,
                        MIN(i.img_path) AS thumb_img_path,
                        NOW()
                    FROM images i
                    WHERE i.thumb_yn = 'Y'
                      AND i.user_id IN ('"' + inClause + '"')
                    GROUP BY i.user_id, ROUND(i.geo_lat::NUMERIC, 2), ROUND(i.geo_long::NUMERIC, 2);
                """;

        String insertLevel2 = """
                    INSERT INTO image_clusters (user_id, cluster_level, cluster_lat, cluster_long, image_count, thumb_img_path, updated_at)
                    SELECT
                        i.user_id,
                        2 AS cluster_level,
                        ROUND(i.geo_lat::NUMERIC, 1) AS cluster_lat,
                        ROUND(i.geo_long::NUMERIC, 1) AS cluster_long,
                        COUNT(*) AS image_count,
                        MIN(i.img_path) AS thumb_img_path,
                        NOW()
                    FROM images i
                    WHERE i.thumb_yn = 'Y'
                      AND i.user_id IN ('"' + inClause + '"')
                    GROUP BY i.user_id, ROUND(i.geo_lat::NUMERIC, 1), ROUND(i.geo_long::NUMERIC, 1);
                """;

        String insertLevel3 = """
                    INSERT INTO image_clusters (user_id, cluster_level, cluster_lat, cluster_long, image_count, thumb_img_path, updated_at)
                    SELECT
                        i.user_id,
                        3 AS cluster_level,
                        ROUND(i.geo_lat::NUMERIC, 0) AS cluster_lat,
                        ROUND(i.geo_long::NUMERIC, 0) AS cluster_long,
                        COUNT(*) AS image_count,
                        MIN(i.img_path) AS thumb_img_path,
                        NOW()
                    FROM images i
                    WHERE i.thumb_yn = 'Y'
                      AND i.user_id IN ('"' + inClause + '"')
                    GROUP BY i.user_id, ROUND(i.geo_lat::NUMERIC, 0), ROUND(i.geo_long::NUMERIC, 0);
                """;

        jdbcTemplate.update(insertLevel1, params);
        jdbcTemplate.update(insertLevel2, params);
        jdbcTemplate.update(insertLevel3, params);

        // 6) checkpoint 갱신
        jdbcTemplate.update("""
                    UPDATE job_checkpoint
                    SET last_success_at = ?
                    WHERE job_name = ?
                """, Timestamp.valueOf(LocalDateTime.now()), "image_cluster_recompute");

        log.info("✅ Completed image cluster recomputation (incremental)");
    }

    /**
     * 🌍 Fetch all precomputed clusters.
     */
    public List<ImageClusterResponse> getAllClusters() {
        List<ImageCluster> clusters = imageClusterRepository.findAll();

        if (clusters.isEmpty()) {
            return Collections.emptyList();
        }

        return clusters.stream()
                .map(c -> ImageClusterResponse.builder()
                        .clusterLat(c.getClusterLat())
                        .clusterLong(c.getClusterLong())
                        .imageCount(c.getImageCount())
                        .thumbImgPath(c.getThumbImgPath())
                        .clusterLevel(c.getClusterLevel())
                        .build())
                .collect(Collectors.toList());
    }

    @Cacheable(value = "userImageClusters", key = "#userId", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getUserClusters(Long userId) {
        List<Map<String, Object>> results = imageRepository.findUserImageClusters(userId);

        return results.stream()
                .map(row -> {
                    Map<String, Object> safe = new HashMap<>();
                    safe.put("clusterLat", row.get("cluster_lat"));
                    safe.put("clusterLong", row.get("cluster_long"));
                    safe.put("imageCount", row.get("image_count"));
                    safe.put("thumbImgPath", row.get("thumb_img_path"));
                    safe.put("clusterLevel", row.get("cluster_level"));
                    return safe;
                })
                .collect(Collectors.toList());
    }
}