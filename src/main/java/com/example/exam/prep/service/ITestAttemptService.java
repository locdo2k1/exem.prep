package com.example.exam.prep.service;

import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.constant.status.TestStatus;

import java.util.UUID;

/**
 * Service interface for managing test attempts.
 * Handles operations related to test attempts in the system.
 */
public interface ITestAttemptService {
    
    /**
     * Starts a new test attempt for a user.
     *
     * @param testId The ID of the test being attempted
     * @param userId The ID of the user attempting the test
     * @return The created TestAttempt with status ONGOING
     */
    TestAttempt startTestAttempt(UUID testId, UUID userId);
    
    /**
     * Submits an ongoing test attempt.
     *
     * @param attemptId The ID of the attempt to submit
     * @param userId The ID of the user submitting the attempt
     * @return The submitted TestAttempt with status SUBMITTED
     * @throws IllegalStateException if the attempt is not in ONGOING status
     */
    TestAttempt submitTestAttempt(UUID attemptId, UUID userId);
    
    /**
     * Updates the status of a test attempt.
     *
     * @param attemptId The ID of the attempt to update
     * @param status The new status to set
     * @return The updated TestAttempt
     */
    TestAttempt updateTestAttemptStatus(UUID attemptId, TestStatus status);
    
    /**
     * Retrieves a test attempt by its ID.
     *
     * @param attemptId The ID of the attempt to retrieve
     * @return The TestAttempt if found
     */
    TestAttempt getTestAttemptById(UUID attemptId);
    
    /**
     * Calculates and updates the total score for a test attempt.
     *
     * @param attemptId The ID of the attempt to update
     * @return The updated TestAttempt with the new total score
     */
    TestAttempt calculateAndUpdateScore(UUID attemptId);
    
    /**
     * Gets the current status of a test attempt.
     *
     * @param attemptId The ID of the attempt
     * @return The current status of the attempt
     */
    TestStatus getTestAttemptStatus(UUID attemptId);
}
