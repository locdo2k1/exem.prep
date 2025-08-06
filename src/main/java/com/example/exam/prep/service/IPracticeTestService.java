package com.example.exam.prep.service;

import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.model.TestPartAttempt;
import com.example.exam.prep.viewmodel.practice_test.PracticePartVM;
import com.example.exam.prep.viewmodel.practice_test.PracticeTestResultVM;
import com.example.exam.prep.viewmodel.practice_test.PracticeTestVM;

import java.util.List;
import java.util.UUID;

public interface IPracticeTestService {
    /**
     * Start a new practice test
     * @param testId The ID of the test to practice
     * @param userId The ID of the user starting the practice
     * @return The started test attempt
     */
    TestAttempt startPracticeTest(UUID testId, UUID userId);

    /**
     * Submit a practice test part
     * @param attemptId The ID of the test part attempt
     * @param userId The ID of the user submitting the attempt
     * @return The submitted test part attempt
     */
    TestPartAttempt submitPracticeTestPart(UUID attemptId, UUID userId);

    /**
     * Get all part attempts for a test attempt
     * @param testAttemptId The ID of the test attempt
     * @return List of test part attempts
     */
    List<TestPartAttempt> getTestPartAttempts(UUID testAttemptId);

    /**
     * Get practice test results
     * @param testAttemptId The ID of the test attempt
     * @param userId The ID of the user requesting the results
     * @return The practice test results
     */
    PracticeTestResultVM getPracticeTestResults(UUID testAttemptId, UUID userId);

    /**
     * Get practice part details by part ID and test ID
     * @param partId The ID of the part to retrieve
     * @param testId The ID of the test that contains this part
     * @return PracticePartVM containing the part details
     */
    PracticePartVM getPracticePartById(UUID partId, UUID testId);
    
    /**
     * Get practice test data for specific parts of a test
     * @param testId The ID of the test
     * @param partIds Optional set of part IDs to include in the practice test
     * @return PracticeTestVM containing the requested test parts
     */
    PracticeTestVM getPracticeTestByParts(UUID testId, java.util.Set<UUID> partIds);
}
