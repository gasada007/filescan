package com.example.filescan.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalVerdict {
    private String verdict;
    private float threatLevel;
    private float confidence;

}
