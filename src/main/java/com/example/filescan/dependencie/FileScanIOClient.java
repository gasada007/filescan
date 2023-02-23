package com.example.filescan.dependencie;

import com.example.filescan.config.ClientConfiguration;
import com.example.filescan.model.ReportResponse;
import com.example.filescan.model.ScanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;

import java.io.File;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(name = "FileScanIO", url = "${filescanio.host}", path = "api", configuration = ClientConfiguration.class)
public interface FileScanIOClient {

    @RequestMapping(method = POST, path = "scan/file", headers = "Content-Type=multipart/form-data")
    ScanResponse scanFile(@RequestPart("file") File file);

    @RequestMapping(method = GET, path = "scan/{flowId}/report")
    ReportResponse report(@PathVariable("flowId") String flowId);
}
