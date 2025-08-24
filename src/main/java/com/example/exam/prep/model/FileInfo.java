package com.example.exam.prep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "file_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo extends BaseEntity {

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String fileName;

    @Column(nullable = false, columnDefinition = "nvarchar(1000)")
    private String filePath;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<TestFile> testFiles = new java.util.HashSet<>();

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    @JsonIgnore
    private Question question;
}
