package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * View model for file information in test results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoResultVM {
   private UUID id;
   private String fileName;
   private String fileUrl;
   private String fileType;
   private Long fileSize;
}
