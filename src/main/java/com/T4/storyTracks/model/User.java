package com.T4.storyTracks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(name = "pwd", nullable = false)
    private String pwd;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "birth_ymd", length = 8)
    private String birthYmd;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "blog_name")
    private String blogName;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "rgst_dtm", columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime rgstDtm;

    @Column(name = "chng_dtm", columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime chngDtm;

    @Column(name = "last_login_dtm")
    private OffsetDateTime lastLoginDtm;
}
