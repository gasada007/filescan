package com.example.filescan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@EnableScheduling
@EnableCaching
@EnableFeignClients
@SpringBootApplication
public class FileScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileScanApplication.class, args);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        TaskScheduler scheduler = new ConcurrentTaskScheduler();
        return scheduler;
    }
}
