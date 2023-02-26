package com.example.filescan.service;

import com.example.filescan.dependencie.FileScanIOClient;
import com.example.filescan.model.FileProcessStatus;
import com.example.filescan.model.Report;
import com.example.filescan.model.ReportResponse;
import com.example.filescan.persistence.entity.FileProcess;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FileReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReportService.class);
    public static final String DEFAULT_REPORT_MESSAGE = "File analyze is in progress. [FlowId: %s] Check in report package (%s) later or use the endpoint.";
    public static final String FINISHED_REPORT_MESSAGE = "File analyze is finished. [FlowId: %s] Result: %s";
    public static final String FAILED_REPORT_MESSAGE = "File analyze is failed. [FlowId: %s] Reason: %s";

    private final CommonService commonService;
    private final FileScanIOClient fileScanIOClient;

    public FileReportService(CommonService commonService, FileScanIOClient fileScanIOClient) {
        this.commonService = commonService;
        this.fileScanIOClient = fileScanIOClient;
    }

    /**
     * This method is responsible for collect reports of scanned files
     */
    @Scheduled(fixedDelayString = "${file.report.delayInSecond}", initialDelay = 1000 * 15)
    public void fileReports() {
        commonService.createMissingFolders();

        List<FileProcess> files = commonService.findByStatus(FileProcessStatus.IN_PROGRESS);

        LOGGER.info("Count(s) of file for report: {}", files.size());

        files.forEach(this::fileReport);
    }

    /**
     * This method is responsible for report of file
     *
     * @param file file which for report
     * @return the current result of scan
     */
    private String fileReport(FileProcess file) {
        String flowId = file.getFlowId();
        String responseData = String.format(DEFAULT_REPORT_MESSAGE, flowId, commonService.reportDir);
        try {
            ReportResponse response = fileScanIOClient.report(flowId);
            LOGGER.debug("Response: {}", response);

            Report report = response.getReports().values().stream().findFirst().orElse(null);
            if (response.isAllFinished() && report != null && report.getFinalVerdict() != null) {
                file.setStatus(FileProcessStatus.FINISHED);
                responseData = String.format(FINISHED_REPORT_MESSAGE, flowId, report.getFinalVerdict());
            }
        } catch (FeignException e) {
            file.setStatus(FileProcessStatus.FAILED);
            responseData = String.format(FAILED_REPORT_MESSAGE, flowId, e.getMessage());
        } catch (Exception e) {
            LOGGER.warn("Error during [{}] file reports", file.getFlowId(), e);
            file.setStatus(FileProcessStatus.FAILED);
            responseData = String.format(FAILED_REPORT_MESSAGE, flowId, e.getMessage());
        } finally {
            file.setLastCheckDate(new Date());
            file.setResponse(responseData);
            commonService.saveFileProcess(file);
            saveInFile(file, flowId, responseData);
            return responseData;
        }
    }

    private void saveInFile(FileProcess file, String flowId, String responseData) {
        try {
            FileWriter myWriter = new FileWriter(commonService.reportDir + "/" + file.getFileName() + "_" + flowId + ".txt");
            myWriter.write(responseData);
            myWriter.close();
        } catch (Exception e) {
            LOGGER.warn("Error during save report in dir under [{}] file reports", file.getFlowId(), e);
        }
    }

    /**
     * This method give back report result if exist
     *
     * @param flowId unique id of scan
     * @return the current result of scan
     */
    public String getFileReport(String flowId) {
        FileProcess fileProcess = commonService.findById(flowId);
        if (fileProcess == null) {
            fileProcess = commonService.createFileProcess(UUID.randomUUID().toString(), flowId);
        }

        if (fileProcess.getStatus() == FileProcessStatus.FINISHED) {
            return fileProcess.getResponse();
        }

        commonService.createMissingFolders();
        return fileReport(fileProcess);
    }
}
