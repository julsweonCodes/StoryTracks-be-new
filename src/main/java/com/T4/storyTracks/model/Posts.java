package com.T4.storyTracks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Time;
import java.sql.Timestamp;
import lombok.*;


/**
 * Entity class representing the 'posts' table in PostgreSQL.
 * Each instance of this class corresponds to one row in the table.
 */

@Entity // Marks this class as a JPA entity mapped to a database table.
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Posts {
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
  private Timestamp rgstDtm;

  @Column(name = "chng_dtm")
  private Timestamp chngDtm;

}
