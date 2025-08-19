package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View Model for representing a test part in analysis results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisPartVM {
    /**
     * Order of the test part
     */
    private Integer order;
    
    /**
     * Name of the test part (e.g., "Listening", "Reading")
     */
    private String partName;

    /**
     * Overall analysis by question categories
     */
    private List<AnalysisQuesCategory> categories;
}
