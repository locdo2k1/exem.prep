package com.example.exam.prep.controller;

import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.ITestResultService;
import com.example.exam.prep.vm.testresult.AnalysisQuestionsVM;
import com.example.exam.prep.vm.testresult.TestResultOverallVM;
import com.example.exam.prep.vm.testresult.TestInfoVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestResultController {

    private final ITestResultService testResultService;

    /**
     * Get overall test results for a specific test and attempt
     * 
     * @param attemptId The ID of the specific attempt (optional, can be null for
     *                  all attempts)
     * @return Overall test results with question details
     */
    @GetMapping("/tests/results/overall/{attemptId}")
    public ApiResponse<TestResultOverallVM> getTestResultOverall(
            @PathVariable UUID attemptId) {
        TestResultOverallVM result = testResultService.getTestResultOverall(attemptId);
        return ApiResponse.success(result);
    }

    /**
     * Get all answers for a specific test attempt
     * 
     * @param attemptId The ID of the specific attempt
     * @return List of test answers with question and selected options
     */
    @GetMapping("/attempts/{attemptId}/answers")
    public ResponseEntity<Object> getTestAnswers(
            @PathVariable UUID attemptId) {
        return ResponseEntity.ok(testResultService.getTestAnswers(attemptId));
    }

    /**
     * Get detailed analysis for a specific test attempt
     * 
     * @param attemptId The ID of the specific attempt
     * @return Detailed analysis including question-wise performance, time spent,
     *         and skill-wise breakdown
     */
    @GetMapping("/attempts/{attemptId}/analysis")
    public ResponseEntity<ApiResponse<AnalysisQuestionsVM>> getTestAttemptAnalysis(
            @PathVariable UUID attemptId) {
        AnalysisQuestionsVM analysis = testResultService.getTestAttemptAnalysis(attemptId);
        return ResponseEntity.ok(ApiResponse.success(analysis));
    }
    
    /**
     * Get test name and part names for a specific test attempt
     * 
     * @param attemptId The ID of the specific attempt
     * @return Test information including test name and list of part names
     */
    @GetMapping("/attempts/{attemptId}/test-info")
    public ResponseEntity<ApiResponse<TestInfoVM>> getTestInfo(
            @PathVariable UUID attemptId) {
        TestInfoVM testInfo = testResultService.getTestInfo(attemptId);
        return ResponseEntity.ok(ApiResponse.success(testInfo));
    }
}
