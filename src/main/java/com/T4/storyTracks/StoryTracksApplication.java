package com.T4.storyTracks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StoryTracksApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoryTracksApplication.class, args);
	}

}
