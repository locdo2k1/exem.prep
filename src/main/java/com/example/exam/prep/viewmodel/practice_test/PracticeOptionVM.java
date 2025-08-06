package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * View model for practice test question options
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeOptionVM {
    private UUID id;
    private String text;
    private int order;

    /**
     * Gets the order index of the option
     * @return the order index
     */
    public int getOrder() {
        return order;
    }
}
