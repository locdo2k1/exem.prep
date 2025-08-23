package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.PracticeTestInfoVM;
import com.example.exam.prep.model.Test;
import com.example.exam.prep.repository.ITestAttemptRepository;
import com.example.exam.prep.repository.ITestQuestionDetailRepository;
import com.example.exam.prep.repository.ITestRepository;
import com.example.exam.prep.service.ITestInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestInfoServiceImpl implements ITestInfoService {
    private final ITestRepository testRepository;
    private final ITestAttemptRepository testAttemptRepository;
    private final ITestQuestionDetailRepository testQuestionDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public PracticeTestInfoVM getTestInfo(UUID testId) {
        // Fetch test with necessary relationships
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test not found with id: " + testId));

        // Calculate statistics
        int questionCount;
        int sectionCount = 0;
        
        if (test.getTestParts() == null || test.getTestParts().isEmpty()) {
            // If test has no parts, count questions from TestQuestionDetail and TestQuestionSetDetail
            int directQuestions = testQuestionDetailRepository.countByTestId(testId);
            int questionSetQuestions = testQuestionDetailRepository.countQuestionsInQuestionSetsByTestId(testId);
            questionCount = directQuestions + questionSetQuestions;
        } else {
            // If test has parts, count questions from TestPartDetail
            questionCount = testQuestionDetailRepository.countQuestionsInTestPartsByTestId(testId);
            sectionCount = test.getTestParts().size();
        }
        int practicedUserCount = testAttemptRepository.countDistinctUsersByTestId(testId);
        
        // TODO: Implement comment count when comment feature is available
        int commentCount = 0; // Placeholder for comment count
        
        // Create and populate DTO
        PracticeTestInfoVM testInfo = new PracticeTestInfoVM();
        testInfo.setDuration(calculateTestDuration(test) + " phút");
        testInfo.setSections(sectionCount);
        testInfo.setQuestions(questionCount);
        testInfo.setComments(commentCount);
        testInfo.setPracticedUsers(practicedUserCount);
        testInfo.setNote(generateTestNote(test));
        
        return testInfo;
    }
    
    private int calculateTestDuration(Test test) {
        // Default duration in minutes
        // Since there's no duration field in TestPart or Part, we'll use a default value
        // You might want to add a duration field to either TestPart or Part in the future
        return 40;
    }
    
    private String generateTestNote(Test test) {
        // TODO: Customize this based on your test requirements
        return "Chú ý: đề được quy đổi sang scaled score (và dy trên thang điểm 9.0 cho TOEIC hoặc 9.0 cho IELTS), vui lòng chọn chế độ làm FULL TEST";
    }

    @Override
    public String getAllTests() {
        // TODO: Implement actual test listing logic
        return "List of tests will be returned here";
    }
}
