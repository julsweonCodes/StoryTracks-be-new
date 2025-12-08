package com.T4.storyTracks.service;

import com.T4.storyTracks.common.ApiResponse;
import com.T4.storyTracks.dto.request.AIGenerateRequest;
import com.T4.storyTracks.dto.response.AiGenerateResponse;
import com.T4.storyTracks.dto.response.ImageClusterResponse;
import com.T4.storyTracks.model.ImageCluster;
import com.T4.storyTracks.repository.ImageClusterRepository;
import com.T4.storyTracks.repository.ImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
     * 🔁 Runs every 12 hours to recompute image clusters for Google Maps.
     */
    @Scheduled(cron = "0 0 */12 * * *")
    public void computeImageClusters() {
        log.info("🛰️ Starting image cluster recomputation...");

        String truncateSql = "TRUNCATE TABLE image_clusters;";
        String insertSql = """
                    TRUNCATE TABLE image_clusters;
                
                    -- Level 1: City (~1 km precision)
                    INSERT INTO image_clusters (cluster_level, cluster_lat, cluster_long, image_count, thumb_img_path, updated_at)
                    SELECT
                        1 AS cluster_level,
                        ROUND(geo_lat::NUMERIC, 2) AS cluster_lat,
                        ROUND(geo_long::NUMERIC, 2) AS cluster_long,
                        COUNT(*) AS image_count,
                        MIN(img_path) AS thumb_img_path,
                        NOW()
                    FROM images
                    WHERE thumb_yn = 'Y'
                    GROUP BY ROUND(geo_lat::NUMERIC, 2), ROUND(geo_long::NUMERIC, 2);
                
                    -- Level 2: Province (~10 km precision)
                    INSERT INTO image_clusters (cluster_level, cluster_lat, cluster_long, image_count, thumb_img_path, updated_at)
                    SELECT
                        2 AS cluster_level,
                        ROUND(geo_lat::NUMERIC, 1) AS cluster_lat,
                        ROUND(geo_long::NUMERIC, 1) AS cluster_long,
                        COUNT(*) AS image_count,
                        MIN(img_path) AS thumb_img_path,
                        NOW()
                    FROM images
                    WHERE thumb_yn = 'Y'
                    GROUP BY ROUND(geo_lat::NUMERIC, 1), ROUND(geo_long::NUMERIC, 1);
                
                    -- Level 3: Country (~100 km precision)
                    INSERT INTO image_clusters (cluster_level, cluster_lat, cluster_long, image_count, thumb_img_path, updated_at)
                    SELECT
                        3 AS cluster_level,
                        ROUND(geo_lat::NUMERIC, 0) AS cluster_lat,
                        ROUND(geo_long::NUMERIC, 0) AS cluster_long,
                        COUNT(*) AS image_count,
                        MIN(img_path) AS thumb_img_path,
                        NOW()
                    FROM images
                    WHERE thumb_yn = 'Y'
                    GROUP BY ROUND(geo_lat::NUMERIC, 0), ROUND(geo_long::NUMERIC, 0);
                """;

        jdbcTemplate.update(truncateSql);
        jdbcTemplate.update(insertSql);

        log.info("✅ Completed image cluster recomputation");
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
                    Map<String,Object> safe = new HashMap<>();
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