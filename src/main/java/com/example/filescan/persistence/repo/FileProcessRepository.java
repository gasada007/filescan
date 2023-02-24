package com.example.filescan.persistence.repo;

import com.example.filescan.model.FileProcessStatus;
import com.example.filescan.persistence.entity.FileProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileProcessRepository extends JpaRepository<FileProcess, String> {
    List<FileProcess> findByStatus(FileProcessStatus status);
}
