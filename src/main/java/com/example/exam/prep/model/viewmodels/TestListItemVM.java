package com.example.exam.prep.model.viewmodels;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View Model representing a test item in a paginated list
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestListItemVM {
    // Test ID
    private UUID id;
    
    // e.g., "Listening, Reading, Writing, Speaking"    
    private List<String> skills;
    
    // e.g., "IELTS"
    private String testName;
    
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
