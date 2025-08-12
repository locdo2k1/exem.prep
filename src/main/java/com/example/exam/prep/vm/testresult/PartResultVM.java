package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View Model for representing a part in a test result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResultVM {
    /**
     * ID of the test part
     */
    private String partId;
    
    /**
     * Name of the test part
     */
    private String name;
    
    /**
     * Order of the part in the test
     */
    private Integer order;
    
    /**
     * List of questions in this part
     */
    private List<QuestionResultVM> questions;
}