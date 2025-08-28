package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.Test;
import com.example.exam.prep.model.TestPart;
import com.example.exam.prep.model.TestPartQuestion;
import com.example.exam.prep.model.TestPartQuestionSet;
import com.example.exam.prep.model.viewmodels.PracticeTestInfoVM;
import com.example.exam.prep.repository.ITestAttemptRepository;
import com.example.exam.prep.repository.ITestQuestionDetailRepository;
import com.example.exam.prep.repository.ITestRepository;
import com.example.exam.prep.repository.ITestPartRepository;
import com.example.exam.prep.service.ITestInfoService;
import com.example.exam.prep.viewmodel.TestPartInfoVM;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestInfoServiceImpl implements ITestInfoService {
    private final ITestRepository testRepository;
    private final ITestAttemptRepository testAttemptRepository;
    private final ITestQuestionDetailRepository testQuestionDetailRepository;
    private final ITestPartRepository testPartRepository;

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
        
        // Get skills from testSkills relationship
        List<String> skills = test.getTestSkills().stream()
            .map(testSkill -> testSkill.getSkill().getName())
            .distinct()
            .collect(Collectors.toList());
        
        // If no skills found, use the legacy skill field as fallback
        if (skills.isEmpty() && test.getSkill() != null && !test.getSkill().isEmpty()) {
            skills = List.of(test.getSkill());
        }
        
        // Build test part info list
        List<TestPartInfoVM> testPartInfoList = new ArrayList<>();
        List<TestPart> parts = testPartRepository.findByTestIdWithParts(testId);
        for (TestPart tp : parts) {
            // Title from Part name; fallback to "Part {orderIndex}"
            String title = tp.getPart() != null && tp.getPart().getName() != null
                    ? tp.getPart().getName()
                    : ("Part " + (tp.getOrderIndex() != null ? tp.getOrderIndex() : 0));

            // Compute question count: direct questions + items from question sets
            int directCount = tp.getTestPartQuestions() != null ? tp.getTestPartQuestions().size() : 0;
            int setItemCount = 0;
            if (tp.getTestPartQuestionSets() != null) {
                for (TestPartQuestionSet tqs : tp.getTestPartQuestionSets()) {
                    if (tqs.getQuestionSet() != null && tqs.getQuestionSet().getQuestionSetItems() != null) {
                        setItemCount += tqs.getQuestionSet().getQuestionSetItems().size();
                    }
                }
            }
            int partQuestionCount = directCount + setItemCount;

            // Collect distinct category names from questions and question set items
            List<String> categories = new ArrayList<>();
            if (tp.getTestPartQuestions() != null) {
                for (TestPartQuestion tpq : tp.getTestPartQuestions()) {
                    if (tpq.getQuestion() != null && tpq.getQuestion().getCategory() != null && tpq.getQuestion().getCategory().getName() != null) {
                        categories.add(tpq.getQuestion().getCategory().getName());
                    }
                }
            }
            if (tp.getTestPartQuestionSets() != null) {
                for (TestPartQuestionSet tqs : tp.getTestPartQuestionSets()) {
                    if (tqs.getQuestionSet() != null && tqs.getQuestionSet().getQuestionSetItems() != null) {
                        tqs.getQuestionSet().getQuestionSetItems().forEach(item -> {
                            if (item.getQuestion() != null && item.getQuestion().getCategory() != null && item.getQuestion().getCategory().getName() != null) {
                                categories.add(item.getQuestion().getCategory().getName());
                            }
                        });
                    }
                }
            }
            List<String> distinctCategories = categories.stream().distinct().collect(Collectors.toList());

            // Set part ID and order
            UUID partId = tp.getPart().getId();
            int order = tp.getOrderIndex() != null ? tp.getOrderIndex() : 0;

            // Create and add TestPartInfoVM with all fields
            TestPartInfoVM partInfo = new TestPartInfoVM();
            partInfo.setId(partId);
            partInfo.setTitle(title);
            partInfo.setOrder(order);
            partInfo.setQuestionCount(partQuestionCount);
            partInfo.setQuestionCategories(distinctCategories);
            testPartInfoList.add(partInfo);
        }
        
        // Sort test parts in ascending order by their order field
        testPartInfoList.sort((a, b) -> Integer.compare(a.getOrder(), b.getOrder()));

        // Create and populate DTO
        PracticeTestInfoVM testInfo = new PracticeTestInfoVM();
        testInfo.setSkills(skills);
        testInfo.setTestName(test.getName());
        testInfo.setDuration(calculateTestDuration(test) + " phút");
        testInfo.setSections(sectionCount);
        testInfo.setQuestions(questionCount);
        testInfo.setComments(commentCount);
        testInfo.setPracticedUsers(practicedUserCount);
        testInfo.setNote(generateTestNote(test));
        testInfo.setTestParts(testPartInfoList);
        
        return testInfo;
    }
    
    private int calculateTestDuration(Test test) {

        return test.getDurationMinutes() != null ? test.getDurationMinutes() : 0;
    }
    
    private String generateTestNote(Test test) {
        // Return the note from the test if it exists, otherwise return a default message
        return test.getNote() != null && !test.getNote().isBlank() 
            ? test.getNote()
            : "";
    }

    @Override
    public String getAllTests() {
        // TODO: Implement actual test listing logic
        return "List of tests will be returned here";
    }
}
