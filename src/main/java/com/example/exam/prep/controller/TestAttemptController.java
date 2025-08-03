package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.TestAttemptResponseMessage;
import com.example.exam.prep.constant.status.TestStatus;
import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.ITestAttemptService;
import com.example.exam.prep.vm.testattempt.TestAttemptVM;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/test-attempts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestAttemptController {

    private final ITestAttemptService testAttemptService;
    private final ModelMapper modelMapper;

    @PostMapping("/start/{testId}")
    public ResponseEntity<ApiResponse<TestAttemptVM>> startTestAttempt(
            @PathVariable UUID testId,
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            TestAttempt attempt = testAttemptService.startTestAttempt(testId, userId);
            TestAttemptVM response = modelMapper.map(attempt, TestAttemptVM.class);
            return ResponseEntity.ok(
                ApiResponse.success(response, TestAttemptResponseMessage.ATTEMPT_STARTED.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ApiResponse<TestAttemptVM>> submitTestAttempt(
            @PathVariable UUID attemptId,
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            TestAttempt attempt = testAttemptService.submitTestAttempt(attemptId, userId);
            TestAttemptVM response = modelMapper.map(attempt, TestAttemptVM.class);
            return ResponseEntity.ok(
                ApiResponse.success(response, TestAttemptResponseMessage.ATTEMPT_SUBMITTED.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("/{attemptId}")
    public ResponseEntity<ApiResponse<TestAttemptVM>> getTestAttempt(
            @PathVariable UUID attemptId,
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            TestAttempt attempt = testAttemptService.getTestAttemptById(attemptId);
            if (!attempt.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied", HttpStatus.FORBIDDEN.value()));
            }
            TestAttemptVM response = modelMapper.map(attempt, TestAttemptVM.class);
            return ResponseEntity.ok(
                ApiResponse.success(response, TestAttemptResponseMessage.ATTEMPT_RETRIEVED.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        }
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<ApiResponse<Page<TestAttemptVM>>> getUserTestAttempts(
            @PathVariable UUID testId,
            @RequestHeader("X-User-Id") UUID userId,
            Pageable pageable) {
        try {
            // Note: This endpoint would need to be implemented in the service layer
            // For now, we'll return an empty page as a placeholder
            return ResponseEntity.ok(
                ApiResponse.success(Page.empty(), TestAttemptResponseMessage.ATTEMPTS_RETRIEVED.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{attemptId}/status")
    public ResponseEntity<ApiResponse<TestStatus>> getTestAttemptStatus(
            @PathVariable UUID attemptId) {
        try {
            TestStatus status = testAttemptService.getTestAttemptStatus(attemptId);
            return ResponseEntity.ok(
                ApiResponse.success(status, TestAttemptResponseMessage.STATUS_RETRIEVED.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        }
    }

    @PutMapping("/{attemptId}/status")
    public ResponseEntity<ApiResponse<TestAttemptVM>> updateTestAttemptStatus(
            @PathVariable UUID attemptId,
            @RequestParam TestStatus status) {
        try {
            TestAttempt attempt = testAttemptService.updateTestAttemptStatus(attemptId, status);
            TestAttemptVM response = modelMapper.map(attempt, TestAttemptVM.class);
            return ResponseEntity.ok(
                ApiResponse.success(response, TestAttemptResponseMessage.STATUS_UPDATED.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/{attemptId}/calculate-score")
    public ResponseEntity<ApiResponse<TestAttemptVM>> calculateScore(
            @PathVariable UUID attemptId) {
        try {
            TestAttempt attempt = testAttemptService.calculateAndUpdateScore(attemptId);
            TestAttemptVM response = modelMapper.map(attempt, TestAttemptVM.class);
            return ResponseEntity.ok(
                ApiResponse.success(response, TestAttemptResponseMessage.SCORE_CALCULATED.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }
}
