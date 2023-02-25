package com.example.filescan.service;

import com.example.filescan.dependencie.FileScanIOClient;
import com.example.filescan.model.ScanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessorService.class);

    private final CommonService commonService;
    private final FileScanIOClient fileScanIOClient;

    public FileProcessorService(CommonService commonService, FileScanIOClient fileScanIOClient) {
        this.commonService = commonService;
        this.fileScanIOClient = fileScanIOClient;
    }

    /**
     * This method is responsible for collect and send files for scan
     */
    @Scheduled(fixedDelayString = "${file.processor.delayInSecond}", initialDelay = 1000 * 5)
    public void processFiles() {
        commonService.createMissingFolders();

        File processPackage = new File(commonService.processDir);
        Set<File> files = Stream.of(processPackage.listFiles()).filter(file -> !file.isDirectory()).collect(Collectors.toSet());

        LOGGER.info("Found files for scan. Count: {}", files.size());

        files.forEach(this::processFile);
    }

    /**
     * This method is responsible for send file for scan
     *
     * @param file file which for analyze
     * @return scan unique id
     */
    public String processFile(File file) {
        try {
            ScanResponse response = fileScanIOClient.scanFile(file);
            LOGGER.debug("Response: {}", response);
            commonService.createFileProcess(file.getName(), response.getFlowId());
            file.renameTo(new File(commonService.finishedDir + "/" + file.getName()));
            return response.getFlowId();
        } catch (Exception e) {
            LOGGER.warn("Error during [{}] file process", file.getName(), e);
            file.renameTo(new File(commonService.failedDir + "/" + file.getName()));
        }
        return null;
    }

    /**
     * Convert MultipartFile to File and call @processFile
     *
     * @param multipartFile a file from rest api request
     * @return flowId  unique id of analyze
     */
    public String processMultipartFile(MultipartFile multipartFile) {
        commonService.createMissingFolders();
        File file = new File(commonService.tempDir + "/" + UUID.randomUUID());
        try {
            InputStream initialStream = multipartFile.getInputStream();
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            try (OutputStream outStream = new FileOutputStream(file)) {
                outStream.write(buffer);
            }
        } catch (Exception e) {
            LOGGER.warn("Error during [{}] process multipart file", multipartFile.getName(), e);
            return null;
        }
        return processFile(file);
    }
}
