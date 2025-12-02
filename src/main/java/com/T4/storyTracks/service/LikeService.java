package com.T4.storyTracks.service;

import com.T4.storyTracks.model.Like;
import com.T4.storyTracks.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public void likePost(Long PostId, Long userId) {
        if (!likeRepository.existsByPostIdAndUserId(PostId, userId)) {
            likeRepository.save(new Like(PostId, userId));
        }
    }

    @Transactional
    public void unlikePost(Long PostId, Long userId) {
        likeRepository.deleteByPostIdAndUserId(PostId, userId);
    }

    public boolean isPostLiked(Long PostId, Long userId) {
        return likeRepository.existsByPostIdAndUserId(PostId, userId);
    }

}
