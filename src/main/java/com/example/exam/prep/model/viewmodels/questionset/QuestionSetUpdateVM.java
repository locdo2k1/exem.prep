package com.example.exam.prep.model.viewmodels.questionset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * View model for updating an existing question set.
 * Supports form-data for file uploads.
 */
@Data
public class QuestionSetUpdateVM {
    
    /**
     * ID of the question set to update.
     */
    private UUID id;
    
    /**
     * Title of the question set.
     * Must not be blank and should be less than 255 characters.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than {max} characters")
    private String title;

    /**
     * Detailed description of the question set.
     * Maximum length is 1000 characters.
     */
    @Size(max = 1000, message = "Description must be less than {max} characters")
    private String description;

    /**
     * Comma-separated list of question IDs to be associated with this question set.
     */
    private String questionIds;
    
    /**
     * Get question IDs as a List of UUID.
     * @return List of question UUIDs
     * @throws IllegalArgumentException if any ID is not a valid UUID
     */
    public List<UUID> getQuestionIdsAsList() {
        if (questionIds == null || questionIds.trim().isEmpty()) {
            return List.of();
        }
        try {
            // Handle both JSON array format ["uuid1","uuid2"] and comma-separated values uuid1,uuid2
            String ids = questionIds.trim();
            if (ids.startsWith("[") && ids.endsWith("]")) {
                // Remove the square brackets and any quotes
                ids = ids.substring(1, ids.length() - 1).replaceAll("\"|'\"", "");
            }
            return Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid question ID format. Expected comma-separated UUIDs or JSON array of UUIDs.", e);
        }
    }
    
    /**
     * Audio files to be uploaded with this question set.
     * Supports multiple file uploads.
     */
    private List<MultipartFile> audioFiles;

    /**
     * Display order of the question set.
     * Must be a positive number if provided.
     */
    private Integer order = 0;
}
