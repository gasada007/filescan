package com.example.filescan;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@EnableCaching
@EnableFeignClients
@SpringBootApplication
public class FileScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileScanApplication.class, args);
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        CaffeineCache profileFractions = new CaffeineCache("maybeNeed",
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
