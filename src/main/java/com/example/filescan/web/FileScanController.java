package com.example.filescan.web;

import com.example.filescan.service.FileProcessorService;
import com.example.filescan.service.FileReportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("api/scan")
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
     * @param file file for analyze
     * @return report of analyze
     */
    @RequestMapping(method = POST, path = "file", consumes = MediaType.ALL_VALUE)
//    String scanFile(@RequestPart("file") File file) {
    String scanFile(@RequestBody File file) {
        fileProcessorService.processFiles();
        return "";
    }

    /**
     * Trigger the file process and report
     */
    @RequestMapping(method = GET, path = "trigger")
    void scanFiles() {
        fileProcessorService.processFiles();
        fileReportService.fileReports();
    }
}
