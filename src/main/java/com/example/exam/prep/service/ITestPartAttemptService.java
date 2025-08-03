package com.example.exam.prep.service;

import com.example.exam.prep.model.TestPartAttempt;
import com.example.exam.prep.constant.status.TestPartStatus;

import java.util.UUID;

/**
 * Service interface for managing test part attempts during practice sessions.
 * Handles individual part attempts within practice tests.
 */
public interface ITestPartAttemptService {
    
    /**
     * Starts a new practice attempt for a test part.
     *
     * @param testPartId The ID of the test part to practice
     * @param userId The ID of the user starting the practice
     * @return The created TestPartAttempt with status ONGOING
     */
    TestPartAttempt startPracticeAttempt(UUID testPartId, UUID userId);
    
    /**
     * Submits an ongoing practice attempt for a test part.
     *
     * @param attemptId The ID of the practice attempt to submit
     * @param userId The ID of the user submitting the attempt
     * @return The submitted TestPartAttempt with status SUBMITTED
     * @throws IllegalStateException if the attempt is not in ONGOING status
     */
    TestPartAttempt submitPracticeAttempt(UUID attemptId, UUID userId);
    
    /**
     * Saves the current progress of an ongoing practice attempt.
     *
     * @param attemptId The ID of the practice attempt to save
     * @param userId The ID of the user saving the attempt
     * @param timeSpentSeconds The time spent on the practice so far
     * @return The updated TestPartAttempt
     */
    TestPartAttempt savePracticeProgress(UUID attemptId, UUID userId, int timeSpentSeconds);
    
    /**
     * Retrieves an active (ONGOING) practice attempt for a user and test part.
     *
     * @param testPartId The ID of the test part being practiced
     * @param userId The ID of the user
     * @return The active practice attempt, or null if none exists
     */
    TestPartAttempt getActivePracticeAttempt(UUID testPartId, UUID userId);
    
    /**
     * Gets the current status of a practice attempt.
     *
     * @param attemptId The ID of the practice attempt
     * @return The status of the attempt
     */
    TestPartStatus getPracticeAttemptStatus(UUID attemptId);
    
    /**
     * Gets the remaining time for an ongoing practice attempt.
     *
     * @param attemptId The ID of the practice attempt
     * @param totalTimeLimitSeconds The total time limit in seconds for this practice part
     * @return The remaining time in seconds, or null if the attempt is not active
     */
    Integer getRemainingPracticeTime(UUID attemptId, int totalTimeLimitSeconds);
    
    /**
     * Validates if a practice attempt can be submitted.
     *
     * @param attemptId The ID of the practice attempt to validate
     * @return true if the attempt can be submitted, false otherwise
     */
    boolean validatePracticeSubmission(UUID attemptId);
    
    /**
     * Gets the calculated score for a submitted practice attempt.
     *
     * @param attemptId The ID of the submitted practice attempt
     * @return The calculated score as a percentage (0-100)
     * @throws IllegalStateException if the attempt is not submitted
     */
    double calculatePracticeScore(UUID attemptId);
}
