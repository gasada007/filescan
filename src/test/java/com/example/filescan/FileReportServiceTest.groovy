package com.example.filescan

import com.example.filescan.dependencie.FileScanIOClient
import com.example.filescan.model.FileProcessStatus
import com.example.filescan.persistence.entity.FileProcess
import com.example.filescan.persistence.repo.FileProcessRepository
import com.example.filescan.service.CommonService
import com.example.filescan.service.FileReportService
import spock.lang.Unroll

class FileReportServiceTest extends HelperBase {

    @Unroll
    def 'fileReports case: #caseName'() {
        setup:
            def fileReportService = new FileReportService(new CommonService(Mock(FileProcessRepository.class)), Mock(FileScanIOClient.class))
            setupPaths(fileReportService.commonService)

            removeFileOrDir("files")
            fileReportService.commonService.createMissingFolders()

        when:
            fileReportService.fileReports()

        then:
            1 * fileReportService.commonService.fileProcessRepository.findByStatus(_) >> files
            callReport * fileReportService.fileScanIOClient.report("flowId") >> reportResp
            saveProcessFile * fileReportService.commonService.fileProcessRepository.save(_) >> { FileProcess fileProcessParam ->
                assert fileProcessParam.fileName == "dummy.txt"
                assert fileProcessParam.flowId == "flowId"
                assert fileProcessParam != null
                assert fileProcessParam.status == expStatus
                assert fileProcessParam.response.contains(expResp)
            }

            0 * _

        and:
            if (checkFileOnPath != null) {
                assert this.fileExist(checkFileOnPath as String) == true
            }

        where:
            caseName               | files              | reportResp                                     || checkFileOnPath                                      | expStatus                     | expResp                                                                                              | callReport | saveProcessFile
            "not reportable file"  | []                 | null                                           || null                                                 | null                          | null                                                                                                 | 0          | 0
            "failed report"        | [getFileProcess()] | null                                           || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FAILED      | "File analyze is failed. [FlowId: flowId] Reason:"                                                   | 1          | 1
            "in progress"          | [getFileProcess()] | getReportResponse(false, null)                 || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.IN_PROGRESS | String.format(FileReportService.DEFAULT_REPORT_MESSAGE, "flowId", REPORT_DIR)                        | 1          | 1
            "malicious finished"   | [getFileProcess()] | getReportResponse(true, MALICIOUS_VERDICT)     || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FINISHED    | String.format(FileReportService.FINISHED_REPORT_MESSAGE, "flowId", MALICIOUS_VERDICT.toString())     | 1          | 1
            "information finished" | [getFileProcess()] | getReportResponse(true, INFORMATIONAL_VERDICT) || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FINISHED    | String.format(FileReportService.FINISHED_REPORT_MESSAGE, "flowId", INFORMATIONAL_VERDICT.toString()) | 1          | 1
    }

    @Unroll
    def 'fileReport case: #caseName'() {
        setup:
            def fileReportService = new FileReportService(new CommonService(Mock(FileProcessRepository.class)), Mock(FileScanIOClient.class))
            setupPaths(fileReportService.commonService)

            removeFileOrDir("files")
            fileReportService.commonService.createMissingFolders()

        when:
            def resp = fileReportService.fileReport(file)

        then:
            callReport * fileReportService.fileScanIOClient.report("flowId") >> reportResp
            saveProcessFile * fileReportService.commonService.fileProcessRepository.save(_) >> { FileProcess fileProcessParam ->
                assert fileProcessParam.fileName == "dummy.txt"
                assert fileProcessParam.flowId == "flowId"
                assert fileProcessParam != null
                assert fileProcessParam.status == expStatus
                assert fileProcessParam.response.contains(expResp)
            }

            0 * _

        and:
            assert this.fileExist(checkFileOnPath as String) == true
            assert resp.contains(expResp)

        where:
            caseName               | file             | reportResp                                     || checkFileOnPath                                      | expStatus                     | expResp                                                                                              | callReport | saveProcessFile
            "failed report"        | getFileProcess() | null                                           || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FAILED      | "File analyze is failed. [FlowId: flowId] Reason:"                                                   | 1          | 1
            "in progress"          | getFileProcess() | getReportResponse(false, null)                 || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.IN_PROGRESS | String.format(FileReportService.DEFAULT_REPORT_MESSAGE, "flowId", REPORT_DIR)                        | 1          | 1
            "malicious finished"   | getFileProcess() | getReportResponse(true, MALICIOUS_VERDICT)     || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FINISHED    | String.format(FileReportService.FINISHED_REPORT_MESSAGE, "flowId", MALICIOUS_VERDICT.toString())     | 1          | 1
            "information finished" | getFileProcess() | getReportResponse(true, INFORMATIONAL_VERDICT) || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FINISHED    | String.format(FileReportService.FINISHED_REPORT_MESSAGE, "flowId", INFORMATIONAL_VERDICT.toString()) | 1          | 1
    }

    @Unroll
    def 'getFileReport case: #caseName'() {
        setup:
            def fileReportService = new FileReportService(new CommonService(Mock(FileProcessRepository.class)), Mock(FileScanIOClient.class))
            setupPaths(fileReportService.commonService)

            removeFileOrDir("files")
            fileReportService.commonService.createMissingFolders()

        when:
            def resp = fileReportService.getFileReport("flowId")

        then:
            1 * fileReportService.commonService.fileProcessRepository.findById("flowId") >> Optional.ofNullable(fileProcessFromDb)
            saveMissingProcessFile * fileReportService.commonService.fileProcessRepository.save(_) >> { FileProcess fileProcessParam ->
                assert fileProcessParam.fileName != null
                checkFileOnPath = String.valueOf(REPORT_DIR + "/" + fileProcessParam.fileName + "_flowId.txt")
                assert fileProcessParam.flowId == "flowId"
                assert fileProcessParam != null
                assert fileProcessParam.status == FileProcessStatus.IN_PROGRESS
                assert fileProcessParam.response == null

            }

            callReport * fileReportService.fileScanIOClient.report("flowId") >> reportResp
            saveProcessFile * fileReportService.commonService.fileProcessRepository.save(_) >> { FileProcess fileProcessParam ->
                assert fileProcessParam.fileName != null
                assert fileProcessParam.flowId == "flowId"
                assert fileProcessParam != null
                assert fileProcessParam.status == expStatus
                assert fileProcessParam.response.contains(expResp)
            }

            0 * _

        and:
            if (checkFileOnPath != null) {
                assert this.fileExist(checkFileOnPath as String) == true
            }
            assert resp.contains(expResp)

        where:
            caseName                                     | fileProcessFromDb                                            | reportResp                                     || checkFileOnPath                                      | expStatus                  | expResp                                                                                              | saveMissingProcessFile | callReport | saveProcessFile
            "missing and information finished"           | null                                                         | getReportResponse(true, INFORMATIONAL_VERDICT) || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FINISHED | String.format(FileReportService.FINISHED_REPORT_MESSAGE, "flowId", INFORMATIONAL_VERDICT.toString()) | 1                      | 1          | 1
            "exist and in progress information finished" | getFileProcess(null, FileProcessStatus.IN_PROGRESS)          | getReportResponse(true, INFORMATIONAL_VERDICT) || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FINISHED | String.format(FileReportService.FINISHED_REPORT_MESSAGE, "flowId", INFORMATIONAL_VERDICT.toString()) | 0                      | 1          | 1
            "exist and failed information finished"      | getFileProcess(null, FileProcessStatus.FAILED)               | getReportResponse(true, INFORMATIONAL_VERDICT) || String.valueOf(REPORT_DIR + "/dummy.txt_flowId.txt") | FileProcessStatus.FINISHED | String.format(FileReportService.FINISHED_REPORT_MESSAGE, "flowId", INFORMATIONAL_VERDICT.toString()) | 0                      | 1          | 1
            "exist and finished information finished"    | getFileProcess("Response short", FileProcessStatus.FINISHED) | getReportResponse(true, INFORMATIONAL_VERDICT) || null                                                 | FileProcessStatus.FINISHED | "Response short"                                                                                     | 0                      | 0          | 0
    }
}
