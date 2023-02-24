package com.example.filescan.service;

import com.example.filescan.dependencie.FileScanIOClient;
import com.example.filescan.model.FileProcessStatus;
import com.example.filescan.model.ScanResponse;
import com.example.filescan.persistence.entity.FileProcess;
import com.example.filescan.persistence.repo.FileProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessorService.class);

    private final PackageService packageService;
    private final FileScanIOClient fileScanIOClient;
    private final FileProcessRepository fileProcessRepository;

    public FileProcessorService(PackageService packageService, FileScanIOClient fileScanIOClient, FileProcessRepository fileProcessRepository) {
        this.packageService = packageService;
        this.fileScanIOClient = fileScanIOClient;
        this.fileProcessRepository = fileProcessRepository;
    }

    /**
     * This method is responsible for collect and send files for scan
     */
    @Scheduled(fixedDelayString = "${file.processor.delayInSecond}", initialDelay = 1000 * 5)
    public void processFiles() {
        packageService.createMissingFolders();

        File processPackage = new File(packageService.processDir);
        Set<File> files = Stream.of(processPackage.listFiles()).filter(file -> !file.isDirectory()).collect(Collectors.toSet());

        LOGGER.info("Found files for report. Count: {}", files.size());

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
            saveFileProcess(file.getName(), response.getFlowId());
            file.renameTo(new File(packageService.finishedDir + "/" + file.getName()));
            return response.getFlowId();
        } catch (Exception e) {
            LOGGER.warn("Error during [{}] file process", file.getName(), e);
            file.renameTo(new File(packageService.failedDir + "/" + file.getName()));
        }
        return null;
    }

    /**
     * Create an entity object for FileProcess
     *
     * @param fileName name of file which was analyzed
     * @param flowId   unique id of analyze
     */
    private void saveFileProcess(String fileName, String flowId) {
        FileProcess fileProcess = new FileProcess();
        fileProcess.setFileName(fileName);
        fileProcess.setFlowId(flowId);
        fileProcess.setLastCheckDate(new Date());
        fileProcess.setStatus(FileProcessStatus.IN_PROGRESS);
        fileProcessRepository.save(fileProcess);
    }

    /**
     * Convert MultipartFile to File and call @processFile
     *
     * @param multipartFile a file from rest api request
     * @return flowId  unique id of analyze
     */
    public String processMultipartFile(MultipartFile multipartFile) {
        packageService.createMissingFolders();
        File file = new File(packageService.tempDir + "/" + UUID.randomUUID());
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
