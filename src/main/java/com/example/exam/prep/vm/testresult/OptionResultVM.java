package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View Model for representing an option in a test result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResultVM {
    /**
     * ID of the option
     */
    private String id;
    
    /**
     * Display text of the option
     */
    private String text;
    
    /**
     * Indicates if this option was selected by the user
     */
    private boolean isSelected;
    
    /**
     * Indicates if this option is a correct answer
     */
    private boolean isCorrect;
}
