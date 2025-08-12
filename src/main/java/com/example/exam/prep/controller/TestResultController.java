package com.example.exam.prep.controller;

import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.ITestResultService;
import com.example.exam.prep.vm.testresult.TestResultOverallVM;
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
     * @param testId    The ID of the test
     * @param attemptId The ID of the specific attempt (optional, can be null for
     *                  all attempts)
     * @return Overall test results with question details
     */
    @GetMapping("/tests/{testId}/results/overall/{attemptId}")
    public ResponseEntity<ApiResponse<TestResultOverallVM>> getTestResultOverall(
            @PathVariable UUID testId,
            @PathVariable UUID attemptId) {
        TestResultOverallVM result = testResultService.getTestResultOverall(testId, attemptId);
        return ResponseEntity.ok(ApiResponse.success(result));
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
}
