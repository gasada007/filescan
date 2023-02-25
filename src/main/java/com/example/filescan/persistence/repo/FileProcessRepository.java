package com.example.filescan.persistence.repo;

import com.example.filescan.model.FileProcessStatus;
import com.example.filescan.persistence.entity.FileProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileProcessRepository extends JpaRepository<FileProcess, String> {
    /**
     * Collect file process entities by status
     *
     * @param status status of file process entity
     * @return list of {@link FileProcess} entity
     */
    List<FileProcess> findByStatus(FileProcessStatus status);
}
