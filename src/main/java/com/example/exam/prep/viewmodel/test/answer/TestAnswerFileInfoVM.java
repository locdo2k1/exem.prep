package com.example.exam.prep.viewmodel.test.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * View model for file information in test answers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerFileInfoVM {
   private UUID id;
   private String fileName;
   private String fileUrl;
   private String fileType;
   private Long fileSize;
}
