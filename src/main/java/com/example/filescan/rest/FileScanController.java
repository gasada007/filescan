package com.example.filescan.rest;

import com.example.filescan.service.FileProcessorService;
import com.example.filescan.service.FileReportService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("api")
public class FileScanController {

    private final FileProcessorService fileProcessorService;
    private final FileReportService fileReportService;

    public FileScanController(FileProcessorService fileProcessorService, FileReportService fileReportService) {
        this.fileProcessorService = fileProcessorService;
        this.fileReportService = fileReportService;
    }

    /**
     * Analyze a file and give back report
     *
     * @param multipartFile file for analyze
     * @return the current report of analyze
     */
    @RequestMapping(method = POST, path = "scan/file")
    String scanFile(@RequestParam("file") MultipartFile multipartFile) {
        String flowId = fileProcessorService.processMultipartFile(multipartFile, UUID.randomUUID().toString());
        if (flowId == null) {
            return "Something wrong happened with file process try again later or with other file.";
        }
        return fileReportService.getFileReport(flowId);
    }

    /**
     * Collect the report for scan
     */
    @RequestMapping(method = GET, path = "report")
    String getReport(@NotNull @RequestParam("flowId") String flowId) {
        return fileReportService.getFileReport(flowId);
    }

    /**
     * Trigger the file process and report
     */
    @RequestMapping(method = GET, path = "scan/trigger")
    void scanFiles() {
        fileProcessorService.processFiles();
        fileReportService.fileReports();
    }
}
