package com.T4.storyTracks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imgId; // Primary key (auto-increment)

    @Column(nullable = false, length = 512)
    private String imgPath; // Image path or URL

    @Column(nullable = false, length = 512)
    private String imgFileName; // Image path or URL

    private String geoLat;  // Latitude (more precise)
    private String geoLong; // Longitude
    private OffsetDateTime imgDtm;
    private OffsetDateTime rgstDtm;
    @Column(name = "thumb_yn", length = 1)
    private String thumbYn; // 'Y' or 'N'

    // 🔗 Many images belong to one post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
