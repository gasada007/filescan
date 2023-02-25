package com.example.filescan.service;

import com.example.filescan.model.FileProcessStatus;
import com.example.filescan.persistence.entity.FileProcess;
import com.example.filescan.persistence.repo.FileProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class CommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
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

    private final FileProcessRepository fileProcessRepository;

    public CommonService(FileProcessRepository fileProcessRepository) {
        this.fileProcessRepository = fileProcessRepository;
    }


    /**
     * Create missing folders for prevent IO exceptions
     */
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

    /**
     * Create an entity object for FileProcess
     *
     * @param fileName name of file which was analyzed
     * @param flowId   unique id of analyze
     * @return new {@link FileProcess} entity
     */
    public FileProcess createFileProcess(String fileName, String flowId) {
        FileProcess fileProcess = new FileProcess();
        fileProcess.setFileName(fileName);
        fileProcess.setFlowId(flowId);
        fileProcess.setLastCheckDate(new Date());
        fileProcess.setStatus(FileProcessStatus.IN_PROGRESS);
        saveFileProcess(fileProcess);
        return fileProcess;
    }

    public void saveFileProcess(FileProcess fileProcess) {
        fileProcessRepository.save(fileProcess);
    }

    public List<FileProcess> findByStatus(FileProcessStatus status) {
        return fileProcessRepository.findByStatus(status);
    }

    public FileProcess findById(String flowId) {
        return fileProcessRepository.findById(flowId).orElse(null);
    }
}
