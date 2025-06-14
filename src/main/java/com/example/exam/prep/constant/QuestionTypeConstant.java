package com.example.exam.prep.constant;

public enum QuestionTypeConstant {
    FILL_IN_THE_BLANK("Fill in the Blank"),
    MULTIPLE_CHOICE("Multiple Choice");
    
    private final String displayName;
    
    QuestionTypeConstant(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
