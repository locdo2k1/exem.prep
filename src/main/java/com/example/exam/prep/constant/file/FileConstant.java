package com.example.exam.prep.constant.file;

/**
 * Constants for file storage and handling
 */
public enum FileConstant {
    // Base directory for question-related files
    QUESTION_FILES("Question Audios/"),
    
    // Maximum file size (10MB)
    MAX_FILE_SIZE(10 * 1024 * 1024),
    
    // Allowed audio MIME types
    ALLOWED_AUDIO_TYPES("audio/mpeg,audio/wav,audio/ogg,audio/mp4,audio/x-m4a"),
    
    // Default file extension for audio files
    DEFAULT_AUDIO_EXTENSION(".mp3"),
    
    // Temporary file prefix
    TEMP_FILE_PREFIX("temp_");

    private final Object value;

    FileConstant(Object value) {
        this.value = value;
    }

    public String getStringValue() {
        return value.toString();
    }

    public int getIntValue() {
        if (value instanceof Integer) {
            return (int) value;
        }
        throw new UnsupportedOperationException("This constant does not have an integer value");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
