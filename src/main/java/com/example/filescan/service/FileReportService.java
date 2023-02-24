package com.example.filescan.service;

import com.example.filescan.dependencie.FileScanIOClient;
import com.example.filescan.model.FileProcessStatus;
import com.example.filescan.model.Report;
import com.example.filescan.model.ReportResponse;
import com.example.filescan.persistence.entity.FileProcess;
import com.example.filescan.persistence.repo.FileProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.util.Date;
import java.util.List;

@Service
public class FileReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReportService.class);

    @Value("${files.report.dir}")
    private String reportDir;

    private final FileScanIOClient fileScanIOClient;
    private final FileProcessRepository fileProcessRepository;

    public FileReportService(FileScanIOClient fileScanIOClient, FileProcessRepository fileProcessRepository) {
        this.fileScanIOClient = fileScanIOClient;
        this.fileProcessRepository = fileProcessRepository;
    }

    @Scheduled(fixedDelay = 1000 * 60, initialDelay = 1000 * 15)
    public void fileReports() {
        LOGGER.info("File reports starting!");

        List<FileProcess> files = fileProcessRepository.findByStatus(FileProcessStatus.IN_PROGRESS);

        LOGGER.info("Found file process. Count: {}", files.size());

        for (FileProcess file : files) {
            try {
                ReportResponse response = fileScanIOClient.report(file.getFlowId());
                LOGGER.info("Response: {}", response);

                FileWriter myWriter = new FileWriter(reportDir + "/" + file.getFileName() + "_" + file.getFlowId() + ".txt");

                Report report = response.getReports().values().stream().findFirst().orElse(null);
                if (response.isAllFinished() && report != null && report.getFinalVerdict() != null) {
                    file.setStatus(FileProcessStatus.FINISHED);
                    myWriter.write("File analyze is finished. Result: " + report.getFinalVerdict().toString());
                } else {
                    myWriter.write("File analyze is in progress.");
                }

                file.setLastCheckDate(new Date());
                fileProcessRepository.save(file);
                myWriter.close();
            } catch (Exception e) {
                LOGGER.warn("Error during [{}] file reports", file.getFlowId(), e);
            }
        }

        LOGGER.info("File reports finished!");
    }
}
