package com.example.exam.prep.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View Model representing practice test information with details about duration, sections, questions, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeTestInfoVM {
    // e.g., "40 phút"
    private String duration;
    
    // e.g., 4
    private int sections;
    
    // e.g., 40
    private int questions;
    
    // e.g., 67
    private int comments;
    
    // e.g., 114679
    private int practicedUsers;
    
    // Additional notes about the test
    private String note;
}
