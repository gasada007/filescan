package com.example.filescan.persistence.entity;

import com.example.filescan.model.FileProcessStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileProcess {

    @Id
    private String flowId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileProcessStatus status;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastCheckDate;

    @Column(length = 5000)
    private String response;
}
