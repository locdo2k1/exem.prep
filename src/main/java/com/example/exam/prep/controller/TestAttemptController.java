package com.example.exam.prep.controller;

import com.example.exam.prep.constant.status.TestStatus;
import com.example.exam.prep.model.viewmodels.TestAttemptWithNameVM;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.ITestAttemptService;
import com.example.exam.prep.vm.testattempt.TestAttemptVM;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-attempts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestAttemptController {
    private final ITestAttemptService testAttemptService;

    @PostMapping("/start/{testId}")
    public ResponseEntity<ApiResponse<TestAttemptVM>> startTestAttempt(
            @PathVariable UUID testId,
            @RequestHeader("X-User-Id") UUID userId) {
        return null;
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ApiResponse<TestAttemptVM>> submitTestAttempt(
            @PathVariable UUID attemptId,
            @RequestHeader("X-User-Id") UUID userId) {
        return null;
    }

    @GetMapping("/{attemptId}")
    public ResponseEntity<ApiResponse<TestAttemptVM>> getTestAttempt(
            @PathVariable UUID attemptId,
            @RequestHeader("X-User-Id") UUID userId) {
        return null;
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<ApiResponse<Page<TestAttemptVM>>> getUserTestAttempts(
            @PathVariable UUID testId,
            @RequestHeader("X-User-Id") UUID userId,
            Pageable pageable) {
        return null;
    }

    @GetMapping("/{attemptId}/status")
    public ResponseEntity<ApiResponse<TestStatus>> getTestAttemptStatus(
            @PathVariable UUID attemptId) {
        return null;
    }

    @PutMapping("/{attemptId}/status")
    public ResponseEntity<ApiResponse<TestAttemptVM>> updateTestAttemptStatus(
            @PathVariable UUID attemptId,
            @RequestParam TestStatus status) {
        return null;
    }

    @PostMapping("/{attemptId}/calculate-score")
    public ResponseEntity<ApiResponse<TestAttemptVM>> calculateScore(
            @PathVariable UUID attemptId) {
        return null;
    }

    /**
     * Get the latest test attempts for a user with test names
     *
     * @param userId The ID of the user
     * @param limit Maximum number of attempts to return (default: 2)
     * @param timezone The timezone to use for date/time formatting (e.g., "Asia/Ho_Chi_Minh")
     * @return List of test attempts with test names
     */
    @PostMapping("/latest")
    public ResponseEntity<ApiResponse<List<TestAttemptWithNameVM>>> getLatestTestAttempts(
            @RequestParam("userId") UUID userId,
            @RequestParam(defaultValue = "2") int limit,
            @RequestParam(required = false) String timezone) {
        
        if (limit < 1) {
            limit = 2; // Default to 2 if invalid limit provided
        }
        
        List<TestAttemptWithNameVM> attempts = testAttemptService.getLatestTestAttemptsWithTestName(userId, limit, timezone);
        return ResponseEntity.ok(ApiResponse.success(attempts));
    }
}
