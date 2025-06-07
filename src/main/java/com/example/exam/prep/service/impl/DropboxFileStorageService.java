package com.example.exam.prep.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.example.exam.prep.model.FileInfo;
import com.example.exam.prep.service.IFileStorageService;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class DropboxFileStorageService implements IFileStorageService {

    private final DbxClientV2 dropboxClient;
    private final IUnitOfWork unitOfWork;
    private static final long TEMP_LINK_DURATION = 4L * 60 * 60; // 4 hours in seconds

    public DropboxFileStorageService(
            @Value("${dropbox.access.token}") String dropboxAccessToken,
            IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
        DbxRequestConfig config = DbxRequestConfig.newBuilder("exam-prep").build();
        this.dropboxClient = new DbxClientV2(config, dropboxAccessToken);
    }

    @Override
    @Transactional
    public FileInfo uploadFile(MultipartFile file, String path) throws IOException {
        try {
            // Normalize path
            String normalizedPath = normalizePath(path);
            String fileName = file.getOriginalFilename();
            String filePath = "/" + normalizedPath + "/" + fileName;

            // Upload file to Dropbox
            try (InputStream in = file.getInputStream()) {
                FileMetadata metadata = dropboxClient.files().uploadBuilder(filePath)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(in);

                // Save file info to database
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(fileName);
                fileInfo.setFilePath(metadata.getPathDisplay());
                fileInfo.setFileType(file.getContentType());
                fileInfo.setFileSize(metadata.getSize());
                fileInfo.setDropboxFileId(metadata.getId());
                fileInfo.setDropboxPathLower(metadata.getPathLower());
                fileInfo.setDropboxContentHash(metadata.getContentHash());
                fileInfo.setLastModified(convertToLocalDateTime(metadata.getServerModified()));

                // Generate and save temporary URL
                // updateTemporaryUrl(fileInfo);

                return unitOfWork.getFileInfoRepository().save(fileInfo);
            }
        } catch (DbxException e) {
            throw new IOException("Failed to upload file to Dropbox", e);
        }
    }

    @Override
    @Transactional
    public void deleteFile(UUID fileId) throws Exception {
        FileInfo fileInfo = unitOfWork.getFileInfoRepository().findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        try {
            // Delete from Dropbox
            dropboxClient.files().deleteV2(fileInfo.getDropboxFileId());
            
            // Delete from database
            unitOfWork.getFileInfoRepository().deleteById(fileId);
        } catch (DbxException e) {
            throw new Exception("Failed to delete file from Dropbox", e);
        }
    }

    @Override
    public FileInfo getFileInfo(UUID fileId) {
        return unitOfWork.getFileInfoRepository().findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

    @Override
    public List<FileInfo> getFilesByPath(String path) {
        String normalizedPath = normalizePath(path);
        return unitOfWork.getFileInfoRepository().findByFilePathContaining(normalizedPath);
    }

    @Override
    public String getTemporaryLink(UUID fileId) throws Exception {
        FileInfo fileInfo = getFileInfo(fileId);
        
        // Check if we have a valid URL that hasn't expired
        if (fileInfo.getUrl() != null && fileInfo.getUrlExpiresAt() != null && 
            fileInfo.getUrlExpiresAt().isAfter(LocalDateTime.now())) {
            return fileInfo.getUrl();
        }
        
        // Generate new temporary link if needed
        // updateTemporaryUrl(fileInfo);
        unitOfWork.getFileInfoRepository().save(fileInfo);
        
        return fileInfo.getUrl();
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            dropboxClient.files().getMetadata(filePath);
            return true;
        } catch (GetMetadataErrorException e) {
            if (e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound()) {
                return false;
            }
            throw new RuntimeException("Error checking file existence", e);
        } catch (DbxException e) {
            throw new RuntimeException("Error accessing Dropbox", e);
        }
    }

    @Override
    public byte[] downloadFile(UUID fileId) throws Exception {
        try {
            // Get file info to get the path
            FileInfo fileInfo = unitOfWork.getFileInfoRepository().findById(fileId)
                    .orElseThrow(() -> new Exception("File not found with id: " + fileId));
            
            // Create output stream to store the file data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            // Download the file from Dropbox
            dropboxClient.files().downloadBuilder(fileInfo.getFilePath())
                    .download(outputStream);
            
            return outputStream.toByteArray();
        } catch (DbxException e) {
            throw new Exception("Error downloading file from Dropbox", e);
        }
    }

    private void updateTemporaryUrl(FileInfo fileInfo) {
        try {
            SharedLinkMetadata sharedLink = dropboxClient.sharing().createSharedLinkWithSettings(
                    fileInfo.getDropboxPathLower());
            
            // Convert to direct download link
            String directUrl = sharedLink.getUrl().replace("www.dropbox.com", "dl.dropboxusercontent.com")
                    .replace("?dl=0", "");
            
            fileInfo.setUrl(directUrl);
            fileInfo.setUrlExpiresAt(LocalDateTime.now().plusHours(4)); // 4 hours from now
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate temporary URL", e);
        }
    }

    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        // Remove leading/trailing slashes and normalize
        return path.replaceAll("^/+|/+$", "").replaceAll("/+", "/");
    }

    private LocalDateTime convertToLocalDateTime(java.util.Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
