package com.example.filescan

import com.example.filescan.dependencie.FileScanIOClient
import com.example.filescan.model.FileProcessStatus
import com.example.filescan.model.ScanResponse
import com.example.filescan.persistence.entity.FileProcess
import com.example.filescan.persistence.repo.FileProcessRepository
import com.example.filescan.service.CommonService
import com.example.filescan.service.FileProcessorService
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Unroll

class FileProcessorServiceTest extends HelperBase {

    @Unroll
    def 'processFiles case: #caseName'() {
        setup:
            def fileProcessorService = new FileProcessorService(new CommonService(Mock(FileProcessRepository.class)), Mock(FileScanIOClient.class))
            setupPaths(fileProcessorService.commonService)

            removeFileOrDir("files")
            fileProcessorService.commonService.createMissingFolders()
            if (fileExist) {
                createFile(PROCESS_DIR + "/dummy.txt", "Dummy data" as String)
            }

        when:
            fileProcessorService.processFiles()

        then:
            callScanFile * fileProcessorService.fileScanIOClient.scanFile(_) >> scanResp
            saveProcessFile * fileProcessorService.commonService.fileProcessRepository.save(_) >> { FileProcess fileProcessParam ->
                assert fileProcessParam.fileName == "dummy.txt"
                assert fileProcessParam.flowId == expFlowId
                assert fileProcessParam.lastCheckDate != null
                assert fileProcessParam.status == FileProcessStatus.IN_PROGRESS
                assert fileProcessParam.response == null
            }

            0 * _

        and:
            if (fileNewPath != null) {
                assert this.fileExist(fileNewPath as String) == true
            }

        where:
            caseName              | fileExist | scanResp                   || fileNewPath                                 | expFlowId | callScanFile | saveProcessFile
            "not exist file"      | false     | null                       || null                                        | null      | 0            | 0
            "exist file failed "  | true      | null                       || String.valueOf(FAILED_DIR + "/dummy.txt")   | null      | 1            | 0
            "exist file finished" | true      | new ScanResponse("flowId") || String.valueOf(FINISHED_DIR + "/dummy.txt") | "flowId"  | 1            | 1
    }

    @Unroll
    def 'processFile case: #caseName'() {
        setup:
            def fileProcessorService = new FileProcessorService(new CommonService(Mock(FileProcessRepository.class)), Mock(FileScanIOClient.class))
            setupPaths(fileProcessorService.commonService)

            removeFileOrDir("files")
            fileProcessorService.commonService.createMissingFolders()
            File file = createFile(PROCESS_DIR + "/dummy.txt", "Dummy data" as String)

        when:
            def flowId = fileProcessorService.processFile(file)

        then:
            callScanFile * fileProcessorService.fileScanIOClient.scanFile(_) >> scanResp
            saveProcessFile * fileProcessorService.commonService.fileProcessRepository.save(_) >> { FileProcess fileProcessParam ->
                assert fileProcessParam.fileName == "dummy.txt"
                assert fileProcessParam.flowId == expFlowId
                assert fileProcessParam.lastCheckDate != null
                assert fileProcessParam.status == FileProcessStatus.IN_PROGRESS
                assert fileProcessParam.response == null
            }

            0 * _

        and:
            assert expFlowId == flowId
            assert this.fileExist(fileNewPath as String) == true

        where:
            caseName        | scanResp                   || fileNewPath                                 | expFlowId | callScanFile | saveProcessFile
            "file failed "  | null                       || String.valueOf(FAILED_DIR + "/dummy.txt")   | null      | 1            | 0
            "file finished" | new ScanResponse("flowId") || String.valueOf(FINISHED_DIR + "/dummy.txt") | "flowId"  | 1            | 1
    }


    @Unroll
    def 'processMultipartFile case: #caseName'() {
        setup:
            def fileProcessorService = new FileProcessorService(new CommonService(Mock(FileProcessRepository.class)), Mock(FileScanIOClient.class))
            setupPaths(fileProcessorService.commonService)

            removeFileOrDir("files")
            fileProcessorService.commonService.createMissingFolders()
            File file = createFile(TEMP_DIR + "/dummy.txt", "Dummy data" as String)
            MockMultipartFile multipartFile = new MockMultipartFile('file', file.getBytes())

        when:
            def flowId = fileProcessorService.processMultipartFile(multipartFile, "dummy.txt")

        then:
            callScanFile * fileProcessorService.fileScanIOClient.scanFile(_) >> scanResp
            saveProcessFile * fileProcessorService.commonService.fileProcessRepository.save(_) >> { FileProcess fileProcessParam ->
                assert fileProcessParam.fileName == "dummy.txt"
                assert fileProcessParam.flowId == expFlowId
                assert fileProcessParam.lastCheckDate != null
                assert fileProcessParam.status == FileProcessStatus.IN_PROGRESS
                assert fileProcessParam.response == null
            }

            0 * _

        and:
            assert expFlowId == flowId
            assert this.fileExist(fileNewPath as String) == true

        where:
            caseName        | scanResp                   || fileNewPath                                 | expFlowId | callScanFile | saveProcessFile
            "file failed "  | null                       || String.valueOf(FAILED_DIR + "/dummy.txt")   | null      | 1            | 0
            "file finished" | new ScanResponse("flowId") || String.valueOf(FINISHED_DIR + "/dummy.txt") | "flowId"  | 1            | 1
    }

//    def 'generate lot of file'() {
//        setup:
//            def fileProcessorService = new FileProcessorService(new CommonService(Mock(FileProcessRepository.class)), Mock(FileScanIOClient.class))
//            setupPaths(fileProcessorService.commonService)
//
//            removeFileOrDir("files")
//            fileProcessorService.commonService.createMissingFolders()
//
//        when:
//            for(int i = 0; i < 100; i++) {
//                createFile(PROCESS_DIR + "/dummy_" + i + ".txt", "Dummy data_" + i as String)
//            }
//
//        then:
//            0 * _
//    }
}
