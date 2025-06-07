package com.example.exam.prep.controller;

import com.example.exam.prep.model.FileInfo;
import com.example.exam.prep.service.IFileStorageService;
import com.example.exam.prep.service.FileDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final IFileStorageService fileStorageService;
    private final FileDownloadService fileDownloadService;

    @Autowired
    public FileController(IFileStorageService fileStorageService, FileDownloadService fileDownloadService) {
        this.fileStorageService = fileStorageService;
        this.fileDownloadService = fileDownloadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileInfo> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", defaultValue = "") String path) {
        try {
            FileInfo fileInfo = fileStorageService.uploadFile(file, path);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileInfo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileInfo> getFileInfo(@PathVariable UUID fileId) {
        try {
            FileInfo fileInfo = fileStorageService.getFileInfo(fileId);
            return ResponseEntity.ok(fileInfo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID fileId) {
        try {
            String downloadUrl = fileStorageService.getTemporaryLink(fileId);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, downloadUrl)
                    .build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FileInfo>> listFiles(@RequestParam(value = "path", defaultValue = "") String path) {
        try {
            List<FileInfo> files = fileStorageService.getFilesByPath(path);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID fileId) {
        try {
            fileStorageService.deleteFile(fileId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{fileId}/url")
    public ResponseEntity<String> getFileUrl(@PathVariable UUID fileId) {
        try {
            String url = fileStorageService.getTemporaryLink(fileId);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Downloads a file by its ID
     * @param fileId the ID of the file to download
     * @return ResponseEntity containing the file data with appropriate headers
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFileById(@PathVariable UUID fileId) {
        return fileDownloadService.downloadFile(fileId);
    }
}
