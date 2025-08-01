package com.example.exam.prep.vm.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * View model for detailed Test Part information in the context of test retrieval.
 * This is specifically used for the findById endpoint to separate concerns from test creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestPartDetailVM {
    private UUID id;
    private String title;
    private String description;
    private Integer order;
    private List<TestQuestionItemVM> questionItems = new java.util.ArrayList<>();
    
    // Helper methods
    public int getTotalQuestions() {
        int total = questionItems != null ? questionItems.size() : 0;
        return total;
    }
    
    public int getTotalScore() {
        int total = 0;
        // if (questionItems != null) {
        //     return total;
        // }
        return total;
    }
    
    /**
     * Converts a TestPartVM to TestPartDetailVM
     * @param partVM The TestPartVM to convert
     * @return A new TestPartDetailVM instance
     */
    public static TestPartDetailVM fromTestPartVM(TestPartVM partVM) {
        if (partVM == null) {
            return null;
        }
        
        return TestPartDetailVM.builder()
                .id(partVM.getId())
                .title(partVM.getTitle())
                .description(partVM.getDescription())
                .order(partVM.getOrder())
                // Note: You'll need to handle the conversion of questionItems separately
                // if needed, as the structure is different between the two classes
                .build();
    }
}
