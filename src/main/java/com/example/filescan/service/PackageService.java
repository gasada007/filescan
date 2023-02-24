package com.example.filescan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
public class PackageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageService.class);
    @Value("${files.temp.dir}")
    public String tempDir;
    @Value("${files.process.dir}")
    public String processDir;
    @Value("${files.finished.dir}")
    public String finishedDir;
    @Value("${files.failed.dir}")
    public String failedDir;
    @Value("${files.report.dir}")
    public String reportDir;

    public void createMissingFolders() {
        List<String> dirs = Arrays.asList(tempDir, processDir, finishedDir, failedDir, reportDir);
        dirs.forEach(dirPath -> {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                try {
                    dir.mkdirs();
                    LOGGER.info("Missing folder fixed. Path: {}", dirPath);
                } catch (Exception e) {
                    LOGGER.warn("Error during fix missing folders", e);
                }
            }
        });
    }

}
