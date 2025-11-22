package com.example.exam.prep.vm.test;

import com.example.exam.prep.model.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoVM {
   private UUID id;
   private String fileName;
   private String filePath;
   private String fileType;
   private Long fileSize;
   private String url;
   private LocalDateTime urlExpiresAt;

   public static FileInfoVM fromEntity(FileInfo fileInfo) {
      if (fileInfo == null) {
         return null;
      }

      return FileInfoVM.builder()
            .id(fileInfo.getId())
            .fileName(fileInfo.getFileName())
            .filePath(fileInfo.getFilePath())
            .fileType(fileInfo.getFileType())
            .fileSize(fileInfo.getFileSize())
            .url(fileInfo.getUrl())
            .urlExpiresAt(fileInfo.getUrlExpiresAt())
            .build();
   }
}
