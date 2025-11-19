package com.example.exam.prep.service;

import com.example.exam.prep.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IFileStorageService {
    FileInfo uploadFile(MultipartFile file, String path) throws IOException;
    
    /**
     * Uploads multiple files to the specified path
     * @param files List of files to upload
     * @param path Destination path where files should be stored
     * @return List of FileInfo objects for the uploaded files
     * @throws IOException if an I/O error occurs during file upload
     */
    List<FileInfo> uploadFiles(List<MultipartFile> files, String path) throws IOException;
    void deleteFile(UUID fileId) throws Exception;
    FileInfo getFileInfo(UUID fileId);
    List<FileInfo> getFilesByPath(String path);
    String getTemporaryLink(UUID fileId) throws Exception;
    boolean fileExists(String filePath);

    /**
     * Downloads a file by its ID
     * @param fileId the ID of the file to download
     * @return byte array containing the file data
     * @throws Exception if file is not found or error occurs during download
     */
    byte[] downloadFile(UUID fileId) throws Exception;
    
    /**
     * Creates a shareable link for a file in Dropbox
     * @param path The path of the file in Dropbox
     * @param access The access level for the link (e.g., "viewer")
     * @param allowDownload Whether to allow downloads
     * @param audience The audience for the link (e.g., "public")
     * @param requestedVisibility The visibility of the link (e.g., "public")
     * @return The shareable link information
     * @throws Exception if an error occurs while creating the shareable link
     */
    String createShareableLink(String path, String access, boolean allowDownload, 
                             String audience, String requestedVisibility) throws Exception;
}
