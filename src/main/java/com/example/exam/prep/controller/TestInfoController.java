package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.TestResponseMessage;
import com.example.exam.prep.model.PracticeTestInfoVM;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.ITestInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-info")
public class TestInfoController {

    private final ITestInfoService testInfoService;

    public TestInfoController(ITestInfoService testInfoService) {
        this.testInfoService = testInfoService;
    }

    /**
     * Get test information for a specific test by ID
     * 
     * @param testId The ID of the test to retrieve information for
     * @return PracticeTestInfo with test details
     */
    @GetMapping("/{testId}")
    public ResponseEntity<ApiResponse<PracticeTestInfoVM>> getTestInfo(@PathVariable UUID testId) {
        try {
            PracticeTestInfoVM testInfo = testInfoService.getTestInfo(testId);
            return ResponseEntity.ok(ApiResponse.success(testInfo, TestResponseMessage.TEST_RETRIEVED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Get a list of all available tests (endpoint for future implementation)
     * 
     * @return List of test information
     */
    @GetMapping
    public ResponseEntity<ApiResponse<String>> getAllTests() {
        try {
            String tests = testInfoService.getAllTests();
            return ResponseEntity.ok(ApiResponse.success(tests, TestResponseMessage.TESTS_RETRIEVED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
        }
    }
}
