package com.example.filescan;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import com.github.benmanes.caffeine.cache.Caffeine;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class FileScanApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileScanApplication.class, args);
	}

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();

		CaffeineCache profileFractions = new CaffeineCache("profileFractions",
				Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build());

		cacheManager.setCaches(Arrays.asList(profileFractions));

		return cacheManager;
	}

	@Bean
	public TaskScheduler taskScheduler() {
	    TaskScheduler scheduler = new ConcurrentTaskScheduler();
	    return scheduler;
	}

}
