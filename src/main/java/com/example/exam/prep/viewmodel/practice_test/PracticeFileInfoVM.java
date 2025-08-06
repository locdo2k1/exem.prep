package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * View model for file information in practice tests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeFileInfoVM {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
}
