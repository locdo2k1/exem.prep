package com.example.exam.prep.controller;

import com.example.exam.prep.constant.status.TestStatus;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.vm.testattempt.TestAttemptVM;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/test-attempts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestAttemptController {

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
}
