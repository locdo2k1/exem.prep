package com.example.exam.prep.service;

import com.example.exam.prep.model.TestPartAttempt;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing test part attempts.
 * Handles individual part attempts within tests.
 */
public interface ITestPartAttemptService {
    
    /**
     * Creates a new test part attempt.
     *
     * @param testPartId The ID of the test part
     * @param userId The ID of the user
     * @return The created TestPartAttempt
     */
    TestPartAttempt startPracticeAttempt(UUID testPartId, UUID userId);
    
    /**
     * Submits a test part attempt.
     *
     * @param attemptId The ID of the attempt to submit
     * @param userId The ID of the user submitting the attempt
     * @return The submitted TestPartAttempt
     */
    TestPartAttempt submitPracticeAttempt(UUID attemptId, UUID userId);
    
    /**
     * Saves the progress of a test part attempt.
     *
     * @param attemptId The ID of the attempt to save
     * @param userId The ID of the user
     * @param timeSpentSeconds The time spent on the attempt so far
     * @return The updated TestPartAttempt
     */
    TestPartAttempt savePracticeProgress(UUID attemptId, UUID userId, int timeSpentSeconds);
    
    /**
     * Retrieves an active test part attempt for a user and test part.
     *
     * @param testPartId The ID of the test part
     * @param userId The ID of the user
     * @return The active test part attempt, or null if none exists
     */
    TestPartAttempt getActivePracticeAttempt(UUID testPartId, UUID userId);
    
    /**
     * Gets all test part attempts for a specific test attempt.
     *
     * @param testAttemptId The ID of the test attempt
     * @return List of test part attempts for the specified test attempt
     */
    List<TestPartAttempt> getTestPartAttemptsByTestAttemptId(UUID testAttemptId);
}
