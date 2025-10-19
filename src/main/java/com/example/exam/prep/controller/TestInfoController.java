package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.TestResponseMessage;
import com.example.exam.prep.model.viewmodels.PracticeTestInfoVM;
import com.example.exam.prep.model.viewmodels.TestAttemptInfoVM;
import com.example.exam.prep.model.viewmodels.TestListItemVM;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.ITestInfoService;
import com.example.exam.prep.service.ITestService;
import com.example.exam.prep.viewmodel.test.answer.TestAnswerVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-info")
public class TestInfoController {

    private final ITestInfoService testInfoService;
    private final ITestService testService;

    public TestInfoController(ITestInfoService testInfoService, ITestService testService) {
        this.testInfoService = testInfoService;
        this.testService = testService;
    }

    /**
     * Get test information for a specific test by ID
     * 
     * @param testId The ID of the test to retrieve information for
     * @return PracticeTestInfo with test details
     */
    @GetMapping("/{testId}")
    public ResponseEntity<ApiResponse<PracticeTestInfoVM>> getPracticeTestInfo(@PathVariable UUID testId) {
        try {
            PracticeTestInfoVM testInfo = testInfoService.getPracticeTestInfo(testId);
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

    /**
     * Get test attempt information for a specific test and user
     * 
     * @param testId The ID of the test (required)
     * @param userId The ID of the user (optional) - if not provided, the system
     *               will use the authenticated user's ID
     * @param tz     Optional IANA timezone (e.g., Asia/Ho_Chi_Minh) for localizing
     *               takeDate
     * @return List of TestAttemptInfo with attempt details
     * @throws ResponseStatusException with BAD_REQUEST status if the test ID is
     *                                 invalid or user is not authenticated (when
     *                                 userId is not provided)
     */
    @GetMapping("/{testId}/attempts")
    public ResponseEntity<ApiResponse<List<TestAttemptInfoVM>>> getTestAttempts(
            @PathVariable UUID testId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String tz) {
        try {
            List<TestAttemptInfoVM> attempts = testInfoService.getTestAttempts(testId, userId, tz);
            return ResponseEntity
                    .ok(ApiResponse.success(attempts, TestResponseMessage.TEST_ATTEMPTS_RETRIEVED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Get solutions for a specific test
     * 
     * @param testId The UUID of the test to get solutions for
     * @return ResponseEntity containing test information, parts, and questions with
     *         solutions
     */
    @GetMapping("/{testId}/solutions")
    public ResponseEntity<ApiResponse<TestAnswerVM>> getTestSolutions(@PathVariable UUID testId) {
        try {
            TestAnswerVM answers = testService.testAnswers(testId);
            return ResponseEntity.ok(ApiResponse.success(answers, TestResponseMessage.TEST_RETRIEVED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Get paginated list of tests with optional filtering by test category and keyword search
     * 
     * @param testCategoryId Optional test category ID to filter tests
     * @param keyword Optional keyword to search in test name
     * @param pageable Pagination parameters (page, size, sort)
     * @return Page of TestListItemVM with test information
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<TestListItemVM>>> getTestList(
            @RequestParam(required = false) UUID testCategoryId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "insertedAt") Pageable pageable) {
        try {
            Page<TestListItemVM> testList = testInfoService.getTestList(testCategoryId, keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success(testList, TestResponseMessage.TESTS_RETRIEVED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
        }
    }
}
