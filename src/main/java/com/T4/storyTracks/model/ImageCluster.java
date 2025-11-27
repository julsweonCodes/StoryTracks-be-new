package com.T4.storyTracks.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.*;

/**
 * Entity class representing precomputed clusters for images on Google Maps.
 * Each instance corresponds to one cluster entry in the 'image_clusters' table.
 */

@Entity
@Table(name = "image_clusters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageCluster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clusterId; // Primary key

    @Column(name = "cluster_lat", nullable = false)
    private Double clusterLat; // Cluster latitude (rounded for grouping)

    @Column(name = "cluster_long", nullable = false)
    private Double clusterLong; // Cluster longitude (rounded for grouping)

    @Column(name = "image_count", nullable = false)
    private Integer imageCount; // Number of images in this cluster

    @Column(name = "thumb_img_path", length = 512)
    private String thumbImgPath; // Representative thumbnail image path

    @Column(name = "cluster_level", nullable = false)
    private int clusterLevel;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt; // Last time this cluster was computed
}
