package com.example.exam.prep.controller;

import com.example.exam.prep.model.PracticeTestInfoVM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-info")
public class TestInfoController {

    /**
     * Get test information for a specific test by ID
     * @param testId The ID of the test to retrieve information for
     * @return PracticeTestInfo with test details
     */
    @GetMapping("/{testId}")
    public ResponseEntity<PracticeTestInfoVM> getTestInfo(@PathVariable Long testId) {
        // In a real application, you would fetch this data from a service/repository
        // For now, we'll return mock data
        PracticeTestInfoVM testInfo = new PracticeTestInfoVM();
        testInfo.setDuration("40 phút");
        testInfo.setSections(4);
        testInfo.setQuestions(40);
        testInfo.setComments(67);
        testInfo.setPracticedUsers(114679);
        testInfo.setNote("Chú ý: đề được quy đổi sang scaled score (và dy trên thang điểm 9.0 cho TOEIC hoặc 9.0 cho IELTS), vui lòng chọn chế độ làm FULL TEST");
        
        return ResponseEntity.ok(testInfo);
    }
    
    /**
     * Get a list of all available tests (endpoint for future implementation)
     * @return List of test information
     */
    @GetMapping
    public ResponseEntity<String> getAllTests() {
        // TODO: Implement actual test listing
        return ResponseEntity.ok("List of tests will be returned here");
    }
}
