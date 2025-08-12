package com.example.exam.prep.service;

import com.example.exam.prep.vm.testresult.AnswerResultVM;
import com.example.exam.prep.vm.testresult.TestResultOverallVM;

import java.util.UUID;

/**
 * Service interface for handling test result related operations
 */
public interface ITestResultService {

    /**
     * Retrieves overall test results for a specific test and attempt
     *
     * @param testId    The ID of the test
     * @param attemptId The ID of the specific attempt
     * @return TestResultOverallVM containing the overall test results
     */
    TestResultOverallVM getTestResultOverall(UUID testId, UUID attemptId);
    
    /**
     * Retrieves detailed answers for a specific test attempt
     *
     * @param attemptId The ID of the specific attempt
     * @return AnswerResultVM containing detailed answers and question information
     */
    AnswerResultVM getTestAnswers(UUID attemptId);
}
