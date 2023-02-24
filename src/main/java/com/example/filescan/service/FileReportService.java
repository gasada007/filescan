package com.example.filescan.service;

import com.example.filescan.dependencie.FileScanIOClient;
import com.example.filescan.model.FileProcessStatus;
import com.example.filescan.model.Report;
import com.example.filescan.model.ReportResponse;
import com.example.filescan.persistence.entity.FileProcess;
import com.example.filescan.persistence.repo.FileProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.util.Date;
import java.util.List;

@Service
public class FileReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReportService.class);

    private final PackageService packageService;
    private final FileScanIOClient fileScanIOClient;
    private final FileProcessRepository fileProcessRepository;

    public FileReportService(PackageService packageService, FileScanIOClient fileScanIOClient, FileProcessRepository fileProcessRepository) {
        this.packageService = packageService;
        this.fileScanIOClient = fileScanIOClient;
        this.fileProcessRepository = fileProcessRepository;
    }

    /**
     * This method is responsible for collect reports to scanned files
     */
    @Scheduled(fixedDelayString = "${file.report.delayInSecond}", initialDelay = 1000 * 15)
    public void fileReports() {
        packageService.createMissingFolders();

        List<FileProcess> files = fileProcessRepository.findByStatus(FileProcessStatus.IN_PROGRESS);

        LOGGER.info("Found file for scan. Count: {}", files.size());

        files.forEach(this::fileReport);
    }

    /**
     * This method is responsible for report of file
     *
     * @param file file which for report
     * @return the current result of scan
     */
    private String fileReport(FileProcess file) {
        try {
            String flowId = file.getFlowId();
            ReportResponse response = fileScanIOClient.report(flowId);
            LOGGER.debug("Response: {}", response);

            String responseData = String.format("File analyze is in progress. Check in report package (%s) later or call use the endpoint. FlowId: %s"
                    , packageService.reportDir, flowId);
            Report report = response.getReports().values().stream().findFirst().orElse(null);
            if (response.isAllFinished() && report != null && report.getFinalVerdict() != null) {
                file.setStatus(FileProcessStatus.FINISHED);
                responseData = String.format("File analyze is finished. Result: %s , FlowId: %s", report.getFinalVerdict(), flowId);
            }

            file.setLastCheckDate(new Date());
            file.setResponse(responseData);
            fileProcessRepository.save(file);

            FileWriter myWriter = new FileWriter(packageService.reportDir + "/" + file.getFileName() + "_" + flowId + ".txt");
            myWriter.write(responseData);
            myWriter.close();
            return responseData;
        } catch (Exception e) {
            LOGGER.warn("Error during [{}] file reports", file.getFlowId(), e);
        }
        return null;
    }

    /**
     * This method give back report result if exist
     *
     * @param flowId unique id of scan
     * @return the current result of scan
     */
    public String getFileReport(String flowId) {
        FileProcess fileProcess = fileProcessRepository.findById(flowId).orElse(null);
        if (fileProcess == null) {
            //TODO for future if the application shut down forget every file process
            return "Not existed file process by flow id";
        }

        if (fileProcess.getStatus() == FileProcessStatus.FINISHED) {
            return fileProcess.getResponse();
        }

        packageService.createMissingFolders();
        return fileReport(fileProcess);
    }
}
