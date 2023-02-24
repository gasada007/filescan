package com.example.filescan.service;

import com.example.filescan.dependencie.FileScanIOClient;
import com.example.filescan.model.FileProcessStatus;
import com.example.filescan.model.ScanResponse;
import com.example.filescan.persistence.entity.FileProcess;
import com.example.filescan.persistence.repo.FileProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessorService.class);

    @Value("${files.process.dir}")
    private String processDir;
    @Value("${files.finished.dir}")
    private String finishedDir;
    @Value("${files.failed.dir}")
    private String failedDir;

    private final FileScanIOClient fileScanIOClient;
    private final FileProcessRepository fileProcessRepository;

    public FileProcessorService(FileScanIOClient fileScanIOClient, FileProcessRepository fileProcessRepository) {
        this.fileScanIOClient = fileScanIOClient;
        this.fileProcessRepository = fileProcessRepository;
    }

    @Scheduled(fixedDelay = 1000 * 60, initialDelay = 1000 * 5)
    public void processFiles() {
        Set<File> files = Stream.of(new File(processDir).listFiles()).filter(file -> !file.isDirectory()).collect(Collectors.toSet());

        LOGGER.info("Found files. Count: {}", files.size());

        for (File file : files) {
            try {
                ScanResponse response = fileScanIOClient.scanFile(file);
                LOGGER.info("Response: {}", response);
                fileProcessRepository.save(createFileProcess(file.getName(), response.getFlowId()));

                file.renameTo(new File(finishedDir + "/" + file.getName()));
            } catch (Exception e) {
                LOGGER.warn("Error during [{}] file process", file.getName(), e);
                file.renameTo(new File(failedDir + "/" + file.getName()));
            }
        }
    }

    private FileProcess createFileProcess(String fileName, String flowId) {
        FileProcess fileProcess = new FileProcess();
        fileProcess.setFileName(fileName);
        fileProcess.setFlowId(flowId);
        fileProcess.setLastCheckDate(new Date());
        fileProcess.setStatus(FileProcessStatus.IN_PROGRESS);
        return fileProcess;
    }
}
