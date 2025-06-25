package com.example.exam.prep.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<ValidFileSize, MultipartFile> {
    
    private long maxSizeInBytes;
    
    @Override
    public void initialize(ValidFileSize constraintAnnotation) {
        // Convert MB to bytes
        this.maxSizeInBytes = constraintAnnotation.max() * 1024 * 1024;
    }
    
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Let @NotNull handle null checks
        }
        
        return file.getSize() <= maxSizeInBytes;
    }
}
