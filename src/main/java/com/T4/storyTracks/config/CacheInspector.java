package com.T4.storyTracks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CacheInspector implements CommandLineRunner {
    @Autowired
    ApplicationContext ctx;
    @Override
    public void run(String... args) {
        System.out.println(">>> CacheManager bean: " + ctx.getBean(CacheManager.class).getClass());
    }
}
