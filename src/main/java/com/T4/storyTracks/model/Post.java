package com.T4.storyTracks.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.Where;


/**
 * Entity class representing the 'posts' table in PostgreSQL.
 * Each instance of this class corresponds to one row in the table.
 */

@Entity // Marks this class as a JPA entity mapped to a database table.
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Post {
  @Id // postId is the PK
  @GeneratedValue(strategy = GenerationType.IDENTITY) //Specifies that the database auto-generates the ID value
  private Long postId;
  private String title;

  // Explicitly maps this field to 'og_text' column; stores large text content.
  @Column(name = "og_text", columnDefinition = "TEXT")
  private String ogText;

  // Maps to 'ai_gen_text' column; stores AI-generated text.
  @Column(name = "ai_gen_text", columnDefinition = "TEXT")
  private String aiGenText;

  private String password;

  @Column(name = "rgst_dtm")
  private LocalDateTime rgstDtm;

  @Column(name = "chng_dtm")
  private LocalDateTime chngDtm;

  // 🔗 전체 이미지 리스트
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<PostImage> postImages = new ArrayList<>();

  // 🔗 썸네일 전용 (thumb_yn = 'Y'인 이미지만 가져옴)
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Where(clause = "thumb_yn = 'Y'")
  private List<PostImage> thumbImg = new ArrayList<>();



}
