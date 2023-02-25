package com.example.filescan

import com.example.filescan.model.FileProcessStatus
import com.example.filescan.model.FinalVerdict
import com.example.filescan.model.Report
import com.example.filescan.model.ReportResponse
import com.example.filescan.persistence.entity.FileProcess
import spock.lang.Specification

class HelperBase extends Specification {

    public static final String TEMP_DIR = "files/temp"
    public static final String FINISHED_DIR = "files/finished"
    public static final String FAILED_DIR = "files/failed"
    public static final String PROCESS_DIR = "files/process"
    public static final String REPORT_DIR = "files/report"

    public static final FinalVerdict INFORMATIONAL_VERDICT = new FinalVerdict(verdict: "INFORMATIONAL", threatLevel: 0.1, confidence: 1.0)
    public static final FinalVerdict MALICIOUS_VERDICT = new FinalVerdict(verdict: "MALICIOUS", threatLevel: 10, confidence: 1.0)

    def createFile(String path, String writeData) {
        try {
            FileWriter myWriter = new FileWriter(path)
            myWriter.append(writeData)
            myWriter.close()
            return getExistFile(path)
        } catch (Exception e) {
            System.out.println("createFile" + e)
            return null
        }
    }

    def getExistFile(String path) {
        try {
            File file = new File(path)
            return file.size() > 0 ? file : null
        } catch (Exception e) {
            System.out.println("getFile" + e)
            return null
        }
    }

    def fileExist(String path) {
        try {
            File file = new File(path)
            return file.size() > 0
        } catch (Exception e) {
            System.out.println("fileExist" + e)
            return false
        }
    }

    def removeFileOrDir(String path) {
        try {
            File file = new File(path)
            if (file.directory) {
                file.deleteDir()
            } else {
                file.delete()
            }
        } catch (Exception e) {
            System.out.println("removeFileOrDir" + e)
        }
    }

    def setupPaths(commonService) {
        commonService.tempDir = TEMP_DIR
        commonService.finishedDir = FINISHED_DIR
        commonService.failedDir = FAILED_DIR
        commonService.processDir = PROCESS_DIR
        commonService.reportDir = REPORT_DIR
    }

    def getFileProcess(resp = null, status = FileProcessStatus.IN_PROGRESS) {
        return new FileProcess("flowId", "dummy.txt", status, new Date(), resp)
    }

    def getReportResponse(allFinished, finalVerdict) {
        return new ReportResponse(flowId: "flowId", allFinished: allFinished, reports: ["reportId": new Report(finalVerdict: finalVerdict)])
    }
}
