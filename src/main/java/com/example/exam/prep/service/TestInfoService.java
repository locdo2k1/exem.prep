package com.example.exam.prep.service;

import com.example.exam.prep.model.viewmodels.PracticeTestInfoVM;

public interface TestInfoService {
    /**
     * Get test information for a specific test by ID
     * @param testId The ID of the test to retrieve information for
     * @return PracticeTestInfoVM with test details
     */
    PracticeTestInfoVM getTestInfo(Long testId);
    
    /**
     * Get a list of all available tests
     * @return List of test information
     */
    String getAllTests();
}
