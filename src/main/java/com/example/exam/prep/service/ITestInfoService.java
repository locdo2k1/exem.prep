package com.example.exam.prep.service;

import com.example.exam.prep.model.PracticeTestInfoVM;
import java.util.UUID;

public interface ITestInfoService {
    /**
     * Get test information for a specific test by ID
     * @param testId The ID of the test to retrieve information for
     * @return PracticeTestInfoVM with test details
     */
    PracticeTestInfoVM getTestInfo(UUID testId);
    
    /**
     * Get a list of all available tests
     * @return List of test information
     */
    String getAllTests();
}
