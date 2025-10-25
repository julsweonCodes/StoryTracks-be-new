package com.T4.storyTracks.repository;

import com.T4.storyTracks.model.Post;
import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  //SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.images WHERE p.post_id = ?
  // Prevents N+1 queries
  // ✅ 게시글 상세 조회 (postImages까지 fetch)
  @EntityGraph(attributePaths = "postImages")
  Optional<Post> findByPostId(Long postId);

}
