package com.T4.storyTracks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {
    @CacheEvict(value = "userImageClusters", key = "#userId")
    public void clearUserCache(Long userId) {
        // Evict cache when user uploads new image
        System.out.println("Cache cleared!! 🔪🔪🔪🔪");
    }
}
