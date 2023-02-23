package com.example.filescan.service;

import com.example.filescan.dependencie.FileScanIOClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessorService.class);

    @Value("${files.dir:files}")
    private String dir;

    private final FileScanIOClient fileScanIOClient;

    public FileProcessorService(FileScanIOClient fileScanIOClient) {
        this.fileScanIOClient = fileScanIOClient;
    }

    @Scheduled(fixedDelay = 1000 * 60, initialDelay = 1000 * 5)
    public void processFiles() {
        LOGGER.info("File process starting!");

        Set<File> files = Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory()).collect(Collectors.toSet());

        for (File file : files) {
            try {
                Object o = fileScanIOClient.scanFile(file);
                LOGGER.info("Response: {}", o);
            } catch (Exception e) {
                LOGGER.warn("Error during [{}] file process", file.getName(), e);
            }
        }

        LOGGER.info("File process finished!");
    }
}
