package com.example.exam.prep.repository;

import com.example.exam.prep.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IFileInfoRepository extends JpaRepository<FileInfo, UUID> {
    
    Optional<FileInfo> findByDropboxFileId(String dropboxFileId);
    
    List<FileInfo> findByFilePathContaining(String path);
    
    List<FileInfo> findByFileType(String fileType);
    
    List<FileInfo> findByIsDeletedFalse();
    
    boolean existsByDropboxFileId(String dropboxFileId);
    
    boolean existsByFilePath(String filePath);
}
