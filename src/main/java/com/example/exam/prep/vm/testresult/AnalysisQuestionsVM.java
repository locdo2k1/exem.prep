package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View Model for representing the complete analysis of test questions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisQuestionsVM {
    
    /**
     * List of test parts with their analysis
     */
    private List<AnalysisPartVM> parts;
    
    /**
     * Overall analysis by question categories
     */
    private List<AnalysisQuesCategory> overall;
}
