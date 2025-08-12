package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Root View Model for test result answers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResultVM {
    /**
     * List of test parts with their questions and answers
     */
    private List<PartResultVM> parts;
    
    /**
     * Flattened list of all questions across all parts
     */
    private List<QuestionResultVM> overall;
}
