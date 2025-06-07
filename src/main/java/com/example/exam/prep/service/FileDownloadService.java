package com.example.exam.prep.service;

import com.example.exam.prep.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileDownloadService {


    private final IFileStorageService fileStorageService;

    @Autowired
    public FileDownloadService(IFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Downloads a file by its ID with proper content type and headers
     * @param fileId the ID of the file to download
     * @return ResponseEntity containing the file data and appropriate headers
     */
    public ResponseEntity<byte[]> downloadFile(UUID fileId) {
        try {
            // Get file info to determine content type
            FileInfo fileInfo = fileStorageService.getFileInfo(fileId);
            if (fileInfo == null) {
                return ResponseEntity.notFound().build();
            }

            // Download file content
            byte[] fileContent = fileStorageService.downloadFile(fileId);

            // Determine content type based on file extension
            String contentType = determineContentType(fileInfo.getFileName());

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("inline", fileInfo.getFileName());
            headers.setCacheControl("max-age=3600");
            
            // For certain content types, force download instead of display
            if (contentType.startsWith("application/") && 
                !contentType.equals("application/pdf") && 
                !contentType.equals("application/json")) {
                headers.setContentDispositionFormData("attachment", fileInfo.getFileName());
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    /**
     * Determines the content type based on file extension
     */
    private String determineContentType(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }
        
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt", "csv" -> "text/plain";
            case "mp4" -> "video/mp4";
            case "mp3" -> "audio/mpeg";
            case "zip" -> "application/zip";
            case "rar" -> "application/x-rar-compressed";
            case "json" -> "application/json";
            case "js" -> "application/javascript";
            case "css" -> "text/css";
            case "html", "htm" -> "text/html";
            default -> "application/octet-stream";
        };
    }
}
