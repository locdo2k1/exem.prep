package com.example.exam.prep.model.viewmodels.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileInfoViewModel {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private long fileSize;
}
