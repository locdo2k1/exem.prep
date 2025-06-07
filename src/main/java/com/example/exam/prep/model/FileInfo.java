package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(name = "dropbox_file_id")
    private String dropboxFileId;

    @Column(name = "dropbox_path_lower")
    private String dropboxPathLower;

    @Column(name = "dropbox_content_hash")
    private String dropboxContentHash;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Column(length = 1000)
    private String url;

    @Column(name = "url_expires_at")
    private LocalDateTime urlExpiresAt;
}
