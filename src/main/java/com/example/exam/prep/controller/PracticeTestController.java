package com.example.exam.prep.controller;

import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.model.TestPartAttempt;
import com.example.exam.prep.service.IPracticeTestService;
import com.example.exam.prep.viewmodel.TestAttemptVM;
import com.example.exam.prep.viewmodel.TestPartAttemptVM;
import com.example.exam.prep.viewmodel.practice_test.PracticePartVM;
import com.example.exam.prep.viewmodel.practice_test.PracticeTestResultVM;
import com.example.exam.prep.viewmodel.practice_test.PracticeTestVM;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.exam.prep.model.request.PracticeTestRequest;
import com.example.exam.prep.model.request.SubmitPracticeTestPartRequest;
import com.example.exam.prep.constant.response.PracticeTestResponseConstants;

@RestController
@RequestMapping("/api/practice-tests")
public class PracticeTestController {

    private final IPracticeTestService practiceTestService;

    @Autowired
    public PracticeTestController(IPracticeTestService practiceTestService) {
        this.practiceTestService = practiceTestService;
    }

    /**
     * Start a new practice test
     * 
     * @param testId The ID of the test to practice
     * @param userId The ID of the user starting the practice
     * @return The started test attempt
     */
    @PostMapping("/{testId}/start")
    public ResponseEntity<TestAttemptVM> startPracticeTest(
            @PathVariable UUID testId,
            @RequestParam UUID userId) {

        TestAttempt attempt = practiceTestService.startPracticeTest(testId, userId);
        return ResponseEntity.ok(TestAttemptVM.fromEntity(attempt));
    }

    /**
     * Submit a practice test part
     * 
     * @param request The request containing attemptId and userId
     * @return The submitted test part attempt
     */
    @PostMapping("/attempts/submit")
    public ResponseEntity<ApiResponse<TestAttempt>> submitPracticeTestPart(
            @RequestBody SubmitPracticeTestPartRequest request) {
        TestAttempt attempt = practiceTestService.submitPracticeTestPart(request);
        return ResponseEntity.ok(ApiResponse.success(attempt, 
            PracticeTestResponseConstants.PRACTICE_TEST_RETRIEVED_SUCCESSFULLY.getMessage()));
    }

    /**
     * Get all part attempts for a test attempt
     * 
     * @param testAttemptId The ID of the test attempt
     * @return List of test part attempts
     */
    @GetMapping("/attempts/{testAttemptId}/parts")
    public ResponseEntity<List<TestPartAttemptVM>> getTestPartAttempts(
            @PathVariable UUID testAttemptId) {

        List<TestPartAttempt> attempts = practiceTestService.getTestPartAttempts(testAttemptId);
        List<TestPartAttemptVM> result = attempts.stream()
                .map(TestPartAttemptVM::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Get practice test results
     * 
     * @param testAttemptId The ID of the test attempt
     * @param userId        The ID of the user requesting the results
     * @return The practice test results
     */
    @GetMapping("/attempts/{testAttemptId}/results")
    public ResponseEntity<PracticeTestResultVM> getPracticeTestResults(
            @PathVariable UUID testAttemptId,
            @RequestParam UUID userId) {

        try {
            PracticeTestResultVM resultVM = practiceTestService.getPracticeTestResults(testAttemptId, userId);
            return ResponseEntity.ok(resultVM);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * Get practice part details by ID and test ID
     * 
     * @param partId The ID of the part to retrieve
     * @param testId The ID of the test that contains this part
     * @return PracticePartVM containing the part details
     */
    @GetMapping("/tests/{testId}/parts/{partId}")
    public ResponseEntity<PracticePartVM> getPracticePartById(
            @PathVariable UUID partId,
            @PathVariable UUID testId) {

        PracticePartVM practicePartVM = practiceTestService.getPracticePartById(partId, testId);
        return ResponseEntity.ok(practicePartVM);
    }

    /**
     * Get practice test data for specific parts of a test
     * 
     * @param testId  The ID of the test
     * @param partIds Optional list of part IDs to include in the practice test
     * @return ApiResponse containing PracticeTestVM with the requested test parts
     */
    @PostMapping("/tests/{testId}/practice")
    public ResponseEntity<ApiResponse<PracticeTestVM>> getPracticeTestByParts(
            @PathVariable UUID testId,
            @RequestBody(required = false) PracticeTestRequest request) {

        Set<UUID> partIds = request != null ? request.getPartIds() : null;
        PracticeTestVM practiceTestVM = practiceTestService.getPracticeTestByParts(testId, partIds);
        return ResponseEntity.ok(ApiResponse.success(practiceTestVM,
                PracticeTestResponseConstants.PRACTICE_TEST_RETRIEVED_SUCCESSFULLY.getMessage()));
    }
}
