package com.example.filescan.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private String flowId;
    private boolean allFinished;
    private Map<String, Report> reports;
}
