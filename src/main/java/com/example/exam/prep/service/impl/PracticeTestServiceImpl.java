package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.*;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import jakarta.persistence.EntityNotFoundException;
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
    private final IUnitOfWork unitOfWork;

    @Autowired
    public PracticeTestServiceImpl(ITestAttemptService testAttemptService,
                                 ITestPartAttemptService testPartAttemptService,
                                 PartService partService,
                                 IUnitOfWork unitOfWork) {
        this.testAttemptService = testAttemptService;
        this.testPartAttemptService = testPartAttemptService;
        this.partService = partService;
        this.unitOfWork = unitOfWork;
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
    
    /**
 * Maps a Question entity to a PracticeQuestionVM view model
 */
private PracticeQuestionVM mapQuestionToVM(Question question, int order, Integer customScore) {
    PracticeQuestionVM questionVM = new PracticeQuestionVM();
    questionVM.setId(question.getId());
    questionVM.setPrompt(question.getPrompt());
    
    // Handle QuestionType which is an entity with name and code
    QuestionType questionType = question.getQuestionType();
    if (questionType != null) {
        questionVM.setQuestionType(questionType.getName());
        questionVM.setType(questionType.getName()); // For backward compatibility
    } else {
        questionVM.setQuestionType("");
        questionVM.setType("");
    }
    
    questionVM.setScore(customScore != null ? customScore : question.getScore());
    questionVM.setOrder(order);
    
    // Process options
    if (question.getOptions() != null && !question.getOptions().isEmpty()) {
        List<PracticeOptionVM> optionVMs = question.getOptions().stream()
                .map(option -> {
                    PracticeOptionVM optionVM = new PracticeOptionVM();
                    optionVM.setId(option.getId());
                    optionVM.setText(option.getText());
                    optionVM.setOrder(option.getOrder());
                    return optionVM;
                })
                .sorted(Comparator.comparingInt(PracticeOptionVM::getOrder))
                .collect(Collectors.toList());
        questionVM.setOptions(optionVMs);
    }
    
    
    // Process file infos (e.g., audio files)
    if (question.getFileInfos() != null && !question.getFileInfos().isEmpty()) {
        List<PracticeFileInfoVM> fileInfoVMs = question.getFileInfos().stream()
                .map(this::mapFileInfoToVM)
                .collect(Collectors.toList());
        questionVM.setQuestionAudios(fileInfoVMs);
    }
    
    if (question.getOptions() != null && !question.getOptions().isEmpty()) {
        List<PracticeOptionVM> options = question.getOptions().stream()
                .map(option -> {
                    PracticeOptionVM optionVM = new PracticeOptionVM();
                    optionVM.setId(option.getId());
                    optionVM.setText(option.getText());
                    optionVM.setOrder(option.getOrder());
                    return optionVM;
                })
                .sorted(Comparator.comparingInt(PracticeOptionVM::getOrder))
                .collect(Collectors.toList());
        questionVM.setOptions(options);
    }
    
    // Process file infos (audios, images, etc.)
    if (question.getFileInfos() != null && !question.getFileInfos().isEmpty()) {
        List<PracticeFileInfoVM> fileInfos = question.getFileInfos().stream()
                .map(this::mapFileInfoToVM)
                .collect(Collectors.toList());
        questionVM.setQuestionAudios(fileInfos);
    }
    
    return questionVM;
}

/**
 * Maps a FileInfo entity to a PracticeFileInfoVM view model
 */
private PracticeFileInfoVM mapFileInfoToVM(FileInfo fileInfo) {
    return new PracticeFileInfoVM(
            fileInfo.getId(),
            fileInfo.getFileName(),
            fileInfo.getUrl(),
            fileInfo.getFileType(),
            fileInfo.getFileSize()
    );
}

/**
 * Maps a QuestionSet entity to a PracticeQuestionSetVM view model
 */
private PracticeQuestionSetVM mapQuestionSetToVM(QuestionSet questionSet, int order) {
    PracticeQuestionSetVM questionSetVM = new PracticeQuestionSetVM();
    questionSetVM.setId(questionSet.getId());
    questionSetVM.setTitle(questionSet.getTitle());
    questionSetVM.setDescription(questionSet.getDescription());
    questionSetVM.setOrder(order);
    questionSetVM.setImageUrl(questionSet.getImageUrl());
    
    // Process questions in the question set
    if (questionSet.getQuestionSetItems() != null && !questionSet.getQuestionSetItems().isEmpty()) {
        List<PracticeQuestionVM> questions = questionSet.getQuestionSetItems().stream()
                .filter(QuestionSetItem::getIsActive)
                .sorted(Comparator.comparing(QuestionSetItem::getOrder, 
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(item -> {
                    Question question = item.getQuestion();
                    if (question != null) {
                        return mapQuestionToVM(question, 
                                item.getOrder() != null ? item.getOrder() : 0, 
                                item.getCustomScore());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        questionSetVM.setQuestions(questions);
        questionSetVM.setTotalQuestions(questions.size());
        questionSetVM.setTotalScore(questions.stream()
                .mapToInt(PracticeQuestionVM::getScore)
                .sum());
    } else {
        questionSetVM.setQuestions(Collections.emptyList());
        questionSetVM.setTotalQuestions(0);
        questionSetVM.setTotalScore(0);
    }
    
    return questionSetVM;
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
                                .map(option -> {
                                    PracticeOptionVM optionVM = new PracticeOptionVM();
                                    optionVM.setId(option.getId());
                                    optionVM.setText(option.getText());
                                    optionVM.setOrder(option.getOrder());
                                    return optionVM;
                                })
                                .sorted(Comparator.comparingInt(PracticeOptionVM::getOrder))
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
                                                .map(opt -> new PracticeOptionVM(opt.getId(), opt.getText(), opt.getOrder()))
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

    @Override
    public PracticeTestVM getPracticeTestByParts(UUID testId, Set<UUID> partIds) {
        // Get the test with its relationships
        Test test = unitOfWork.getTestRepository().findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + testId));
        
        // Create the view model
        PracticeTestVM vm = new PracticeTestVM();
        vm.setTestId(test.getId());
        vm.setTestName(test.getName());
        
        // Initialize lists
        List<PracticePartVM> parts = new ArrayList<>();
        List<PracticeQuestionAndQuestionSetVM> allQuestionItems = new ArrayList<>();
        
        // Process test parts if they exist
        if (test.getTestParts() != null && !test.getTestParts().isEmpty()) {
            // Filter parts if partIds is provided and not empty, and sort by orderIndex
            List<TestPart> testParts = test.getTestParts().stream()
                    .filter(testPart -> partIds == null || partIds.isEmpty() || 
                            partIds.contains(testPart.getPart().getId()))
                    .sorted(Comparator.comparing(TestPart::getOrderIndex))
                    .collect(Collectors.toList());
            
            // Process each test part to get its details and questions/question sets
            for (TestPart testPart : testParts) {
                UUID partId = testPart.getPart().getId();
                PracticePartVM partVM = getPracticePartById(partId, testId);
                
                if (partVM != null) {
                    parts.add(partVM);
                }
            }
        }
        
        // Map individual questions from testQuestionDetails
        if (test.getTestQuestionDetails() != null) {
            test.getTestQuestionDetails().stream()
                    .sorted(Comparator.comparing(TestQuestionDetail::getOrder))
                    .map(detail -> {
                        Question question = detail.getQuestion();
                        PracticeQuestionVM questionVM = mapQuestionToVM(question, detail.getOrder(), null);
                        
                        PracticeQuestionAndQuestionSetVM itemVM = new PracticeQuestionAndQuestionSetVM();
                        itemVM.setId(question.getId());
                        itemVM.setOrder(detail.getOrder());
                        itemVM.setQuestion(questionVM);
                        return itemVM;
                    })
                    .forEach(allQuestionItems::add);
        }
        
        // Map question sets from testQuestionSetDetails
        if (test.getTestQuestionSetDetails() != null) {
            test.getTestQuestionSetDetails().stream()
                    .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder))
                    .map(detail -> {
                        QuestionSet questionSet = detail.getQuestionSet();
                        PracticeQuestionSetVM setVM = mapQuestionSetToVM(questionSet, detail.getOrder());
                        
                        PracticeQuestionAndQuestionSetVM itemVM = new PracticeQuestionAndQuestionSetVM();
                        itemVM.setId(questionSet.getId());
                        itemVM.setOrder(detail.getOrder());
                        itemVM.setQuestionSet(setVM);
                        return itemVM;
                    })
                    .forEach(allQuestionItems::add);
        }
        
        // Sort all items by their order
        allQuestionItems.sort(Comparator.comparingInt(PracticeQuestionAndQuestionSetVM::getOrder));
        
        vm.setParts(parts);
        vm.setQuestionAndQuestionSet(allQuestionItems);
        return vm;
    }
}
