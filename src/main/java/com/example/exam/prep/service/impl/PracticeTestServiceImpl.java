package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.*;
import com.example.exam.prep.exception.ResourceNotFoundException;
import com.example.exam.prep.constant.status.TestPartStatus;
import com.example.exam.prep.service.ITestAttemptService;
import com.example.exam.prep.service.ITestPartAttemptService;
import com.example.exam.prep.service.IPracticeTestService;
import com.example.exam.prep.viewmodel.practice_test.PracticeTestResultVM;
import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.model.TestPartAttempt;
import com.example.exam.prep.service.PartService;
import com.example.exam.prep.viewmodel.TestPartAttemptVM;
import com.example.exam.prep.viewmodel.practice_test.PracticePartVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.exam.prep.viewmodel.practice_test.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PracticeTestServiceImpl implements IPracticeTestService {

    private final ITestAttemptService testAttemptService;
    private final ITestPartAttemptService testPartAttemptService;
    private final PartService partService;

    @Autowired
    public PracticeTestServiceImpl(ITestAttemptService testAttemptService,
                                 ITestPartAttemptService testPartAttemptService,
                                 PartService partService) {
        this.testAttemptService = testAttemptService;
        this.testPartAttemptService = testPartAttemptService;
        this.partService = partService;
    }

    @Override
    public TestAttempt startPracticeTest(UUID testId, UUID userId) {
        return testAttemptService.startTestAttempt(testId, userId);
    }

    @Override
    public TestPartAttempt submitPracticeTestPart(UUID attemptId, UUID userId) {
        return testPartAttemptService.submitPracticeAttempt(attemptId, userId);
    }

    @Override
    public List<TestPartAttempt> getTestPartAttempts(UUID testAttemptId) {
        return testPartAttemptService.getTestPartAttemptsByTestAttemptId(testAttemptId);
    }

    @Override
    public PracticeTestResultVM getPracticeTestResults(UUID testAttemptId, UUID userId) {
        TestAttempt attempt = testAttemptService.getTestAttemptById(testAttemptId);
        
        // Verify the user has access to these results
        if (!attempt.getUser().getId().equals(userId)) {
            throw new SecurityException("User is not authorized to access these results");
        }

        // Get all part attempts for this test attempt
        List<TestPartAttempt> partAttempts = testPartAttemptService.getTestPartAttemptsByTestAttemptId(testAttemptId);
        
        // Calculate overall score (only count submitted parts with scores)
        double overallScore = partAttempts.stream()
                .filter(a -> a.getStatus() == TestPartStatus.SUBMITTED && a.getScore() != null)
                .mapToDouble(TestPartAttempt::getScore)
                .average()
                .orElse(0.0);
                
        // Round to 2 decimal places
        overallScore = Math.round(overallScore * 100.0) / 100.0;

        // Create and return the result view model
        PracticeTestResultVM resultVM = new PracticeTestResultVM();
        resultVM.setTestAttemptId(testAttemptId);
        resultVM.setTestId(attempt.getTest().getId());
        resultVM.setUserId(userId);
        resultVM.setOverallScore(overallScore);
        resultVM.setPartResults(partAttempts.stream()
                .map(TestPartAttemptVM::fromEntity)
                .collect(Collectors.toList()));
        
        return resultVM;
    }

    @Override
    public PracticePartVM getPracticePartById(UUID partId, UUID testId) {
        // Get the test part that connects this part to the test
        TestPart testPart = partService.getTestPartByPartIdAndTestId(partId, testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test part not found for partId: " + partId + " and testId: " + testId));
        
        // Get the part with its relationships
        Part part = testPart.getPart();
        
        // Create the view model
        PracticePartVM practicePartVM = new PracticePartVM();
        practicePartVM.setId(part.getId());
        practicePartVM.setName(part.getName());
        
        // Initialize the list to hold both questions and question sets
        List<PracticeQuestionAndQuestionSetVM> questionsAndSets = new ArrayList<>();
        
        // Process individual questions in this test part
        if (testPart.getTestPartQuestions() != null) {
            testPart.getTestPartQuestions().stream()
                .sorted(Comparator.comparing(TestPartQuestion::getDisplayOrder))
                .map(testPartQuestion -> {
                    Question question = testPartQuestion.getQuestion();
                    PracticeQuestionVM questionVM = new PracticeQuestionVM();
                    questionVM.setId(question.getId());
                    questionVM.setText(question.getPrompt());
                    questionVM.setType(question.getQuestionType() != null ? question.getQuestionType().getName() : "");
                    questionVM.setOrder(testPartQuestion.getDisplayOrder());
                    
                    // Process options
                    if (question.getOptions() != null) {
                        List<PracticeOptionVM> options = question.getOptions().stream()
                                .map(option -> new PracticeOptionVM(option.getId(), option.getText()))
                                .collect(Collectors.toList());
                        questionVM.setOptions(options);
                    }
                    

                    
                    PracticeQuestionAndQuestionSetVM vm = new PracticeQuestionAndQuestionSetVM();
                    vm.setId(question.getId());
                    vm.setOrder(testPartQuestion.getDisplayOrder());
                    vm.setQuestion(questionVM);
                    return vm;
                })
                .forEach(questionsAndSets::add);
        }
        
        // Process question sets in this test part
        if (testPart.getTestPartQuestionSets() != null) {
            testPart.getTestPartQuestionSets().stream()
                .sorted(Comparator.comparing(TestPartQuestionSet::getDisplayOrder))
                .map(testPartQuestionSet -> {
                    QuestionSet questionSet = testPartQuestionSet.getQuestionSet();
                    PracticeQuestionSetVM questionSetVM = new PracticeQuestionSetVM();
                    questionSetVM.setId(questionSet.getId());
                    questionSetVM.setTitle(questionSet.getTitle());
                    questionSetVM.setOrder(testPartQuestionSet.getDisplayOrder());
                    
                    // Process questions in this question set
                    if (questionSet.getQuestionSetItems() != null) {
                        List<PracticeQuestionVM> questions = questionSet.getQuestionSetItems().stream()
                                .filter(QuestionSetItem::getIsActive)
                                .sorted(Comparator.comparing(QuestionSetItem::getOrder))
                                .map(item -> {
                                    Question question = item.getQuestion();
                                    PracticeQuestionVM qVM = new PracticeQuestionVM();
                                    qVM.setId(question.getId());
                                    qVM.setText(question.getPrompt());
                                    // Get the question type name from the QuestionType entity
                                    qVM.setType(question.getQuestionType() != null ? question.getQuestionType().getName() : "");
                                    qVM.setOrder(item.getOrder() != null ? item.getOrder() : 0);
                                    
                                    // Process options
                                    if (question.getOptions() != null) {
                                        List<PracticeOptionVM> options = question.getOptions().stream()
                                                .map(opt -> new PracticeOptionVM(opt.getId(), opt.getText()))
                                                .collect(Collectors.toList());
                                        qVM.setOptions(options);
                                    }
                                    
                                    return qVM;
                                })
                                .collect(Collectors.toList());
                        questionSetVM.setQuestions(questions);
                    }
                    
                    PracticeQuestionAndQuestionSetVM vm = new PracticeQuestionAndQuestionSetVM();
                    vm.setId(questionSet.getId());
                    vm.setOrder(testPartQuestionSet.getDisplayOrder());
                    vm.setQuestionSet(questionSetVM);
                    return vm;
                })
                .forEach(questionsAndSets::add);
        }
        
        // Sort all items by their order
        questionsAndSets.sort(Comparator.comparingInt(PracticeQuestionAndQuestionSetVM::getOrder));
        practicePartVM.setQuestionsAndQuestionSets(questionsAndSets);
        
        return practicePartVM;
    }
}
