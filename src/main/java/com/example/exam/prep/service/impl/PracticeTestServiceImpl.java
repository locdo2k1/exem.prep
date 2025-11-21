package com.example.exam.prep.service.impl;

import com.example.exam.prep.constant.status.TestStatus;
import com.example.exam.prep.model.*;
import com.example.exam.prep.model.request.QuestionAnswerRequest;
import com.example.exam.prep.model.request.SubmitPracticeTestPartRequest;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import jakarta.persistence.EntityNotFoundException;
import com.example.exam.prep.exception.ResourceNotFoundException;
import com.example.exam.prep.service.ITestAttemptService;
import com.example.exam.prep.service.ITestPartAttemptService;
import com.example.exam.prep.service.IPracticeTestService;
import com.example.exam.prep.service.PartService;
import com.example.exam.prep.viewmodel.TestPartAttemptVM;
import com.example.exam.prep.util.AuthHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.exam.prep.viewmodel.practice_test.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.example.exam.prep.service.IFileStorageService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PracticeTestServiceImpl implements IPracticeTestService {

    private final ITestAttemptService testAttemptService;
    private final ITestPartAttemptService testPartAttemptService;
    private final PartService partService;
    private final IUnitOfWork unitOfWork;
    private final AuthHelper authHelper;
    private final IFileStorageService fileStorageService;

    @Autowired
    public PracticeTestServiceImpl(ITestAttemptService testAttemptService,
            ITestPartAttemptService testPartAttemptService,
            PartService partService,
            IUnitOfWork unitOfWork,
            AuthHelper authHelper,
            IFileStorageService fileStorageService) {
        this.testAttemptService = testAttemptService;
        this.testPartAttemptService = testPartAttemptService;
        this.partService = partService;
        this.unitOfWork = unitOfWork;
        this.authHelper = authHelper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public TestAttempt startPracticeTest(UUID testId, UUID userId) {
        return testAttemptService.startTestAttempt(testId, userId);
    }

    @Override
    public TestAttempt submitPracticeTestPart(SubmitPracticeTestPartRequest request) {
        // Get the authenticated user ID
        UUID userId = authHelper.getAuthenticatedUserIdOrThrow();

        // Get the user
        User user = unitOfWork.getUserRepository().findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Get the test attempt
        TestAttempt testAttempt = new TestAttempt();
        Test test = unitOfWork.getTestRepository().findById(request.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + request.getTestId()));

        // Set test and user information
        testAttempt.setTest(test);
        testAttempt.setUser(user);
        testAttempt.setDurationSeconds(request.getDuration());
        testAttempt.setStartTime(Instant.now());
        testAttempt.setStatus(TestStatus.ONGOING);

        // Save the test attempt
        testAttempt = unitOfWork.getTestAttemptRepository().save(testAttempt);

        // Skip the loop if listPartId is null or empty
        if (request.getListPartId() != null && !request.getListPartId().isEmpty()) {
            for (UUID partId : request.getListPartId()) {
                if (partId != null) { // Only process non-null partIds
                    var testPartAttempt = new TestPartAttempt();
                    testPartAttempt.setTestAttempt(testAttempt);
                    Part part = unitOfWork.getPartRepository().findById(partId)
                            .orElse(null); // Returns null instead of throwing

                    if (part != null) { // Only proceed if part was found
                        testPartAttempt.setPart(part);
                        unitOfWork.getTestPartAttemptRepository().save(testPartAttempt);
                    }
                }
            }
        }

        // Process the question answers if any
        if (request.getQuestionAnswers() != null && !request.getQuestionAnswers().isEmpty()) {
            for (QuestionAnswerRequest answer : request.getQuestionAnswers()) {
                Question question = unitOfWork.getQuestionRepository().findById(answer.getQuestionId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Question not found with id: " + answer.getQuestionId()));

                QuestionResponse response = new QuestionResponse();
                response.setTestAttempt(testAttempt);
                response.setQuestion(question);
                response.setResponseTime(LocalDateTime.now());

                // Handle text answer if present
                if (answer.getAnswerText() != null && !answer.getAnswerText().trim().isEmpty()) {
                    response.setTextAnswer(answer.getAnswerText());

                    // For text answers, check if there's a correct answer defined
                    if (question.getFillBlankAnswers() != null && !question.getFillBlankAnswers().isEmpty()) {
                        // Check if any of the correct answers match the user's answer
                        // (case-insensitive)
                        boolean isCorrect = question.getFillBlankAnswers().stream()
                                .anyMatch(fillBlank -> answer.getAnswerText().trim()
                                        .equalsIgnoreCase(fillBlank.getAnswerText().trim()));
                        response.setIsCorrect(isCorrect);
                        // Set score based on correctness
                        response.setScore(isCorrect ? question.getScore() : 0.0);
                    }
                    // If no correct answer is defined, we can't determine correctness, so leave it
                    // as null
                }
                // Handle selected options if any
                else if (answer.getSelectedOptionIds() != null && !answer.getSelectedOptionIds().isEmpty()) {
                    // For multiple choice, get all selected options
                    Set<QuestionResponseOption> selectedOptions = new HashSet<>();
                    Set<UUID> selectedOptionIds = new HashSet<>();

                    // Get all correct option IDs for this question
                    List<Option> allOptions = unitOfWork.getOptionRepository().findByQuestionId(question.getId());
                    Set<UUID> correctOptionIds = allOptions.stream()
                            .filter(Option::isCorrect)
                            .map(Option::getId)
                            .collect(Collectors.toSet());

                    // Track selected option IDs and create response options
                    for (UUID optionId : answer.getSelectedOptionIds()) {
                        Option option = unitOfWork.getOptionRepository().findById(optionId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "Option not found with id: " + optionId));
                        QuestionResponseOption qOption = new QuestionResponseOption();
                        qOption.setOption(option);
                        qOption.setQuestionResponse(response);
                        selectedOptions.add(qOption);
                        selectedOptionIds.add(optionId);
                    }
                    response.setSelectedOptions(selectedOptions);

                    // Check if the answer is correct (all correct options selected and no incorrect
                    // ones)
                    boolean isCorrect = selectedOptionIds.containsAll(correctOptionIds) &&
                            correctOptionIds.containsAll(selectedOptionIds);
                    response.setIsCorrect(isCorrect);
                    // Set score based on correctness
                    response.setScore(isCorrect ? question.getScore() : 0.0);
                }

                // Save the response
                unitOfWork.getQuestionResponseRepository().save(response);
            }
        }

        // Submit the test part attempt
        return testAttempt;
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

        // Create and return the result view model
        PracticeTestResultVM resultVM = new PracticeTestResultVM();
        resultVM.setTestAttemptId(testAttemptId);
        resultVM.setTestId(attempt.getTest().getId());
        resultVM.setUserId(userId);
        resultVM.setOverallScore(0.0); // Default score since we removed score calculation
        resultVM.setPartResults(partAttempts.stream()
                .map(TestPartAttemptVM::fromEntity)
                .collect(Collectors.toList()));

        return resultVM;
    }

    @Override
    public List<PracticeQuestionVM> getPracticeQuestionsByAttempt(UUID testAttemptId) {
        // Load all question responses for the given test attempt
        List<QuestionResponse> questionResponses = unitOfWork.getQuestionResponseRepository()
                .findByTestAttemptId(testAttemptId);

        // Map each response to a PracticeQuestionVM and include correctness information
        List<PracticeQuestionVM> questions = new ArrayList<>();
        int order = 0;

        for (QuestionResponse response : questionResponses) {
            Question question = response.getQuestion();
            if (question == null) {
                continue;
            }

            PracticeQuestionVM vm = mapQuestionToVM(question, order++, null);
            vm.setCorrect(java.util.Optional.ofNullable(response.getIsCorrect()));
            questions.add(vm);
        }

        return questions;
    }

    /**
     * Maps a Question entity to a PracticeQuestionVM view model
     */
    private PracticeQuestionVM mapQuestionToVM(Question question, int order, Integer customScore) {
        PracticeQuestionVM questionVM = new PracticeQuestionVM();
        questionVM.setId(question.getId());
        questionVM.setPrompt(question.getPrompt());
        questionVM.setCorrect(java.util.Optional.empty());

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
        String fileUrl = fileInfo.getUrl();

        // If URL is not set, try to create a shareable link
        if (fileUrl == null || fileUrl.isEmpty()) {
            try {
                fileUrl = fileStorageService.createShareableLink(
                        fileInfo.getFilePath(),
                        "viewer", // access level
                        true, // allow download
                        "public", // audience
                        "public" // requested visibility
                );
            } catch (Exception e) {
                // Log the error and use the existing URL (which might be null)
                log.error("Failed to create shareable link for file: " + fileInfo.getFilePath(), e);
            }
        }

        return new PracticeFileInfoVM(
                fileInfo.getId(),
                fileInfo.getFileName(),
                fileUrl,
                fileInfo.getFileType(),
                fileInfo.getFileSize());
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
                                    item.getOrder() != null ? item.getOrder() - 1 + order : 0,
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Test part not found for partId: " + partId + " and testId: " + testId));

        // Get the part with its relationships
        Part part = testPart.getPart();
        if (part == null) {
            throw new ResourceNotFoundException("Part not found for testPart: " + testPart.getId());
        }

        // Create the view model
        PracticePartVM practicePartVM = new PracticePartVM();
        practicePartVM.setId(part.getId());
        practicePartVM.setName(part.getName() != null ? part.getName() : "");

        // Initialize the list to hold both questions and question sets
        List<PracticeQuestionAndQuestionSetVM> questionsAndSets = new ArrayList<>();

        // Process individual questions in this test part
        if (testPart.getTestPartQuestions() != null) {
            testPart.getTestPartQuestions().stream()
                    .filter(Objects::nonNull)
                    .filter(tpq -> tpq.getQuestion() != null)
                    .sorted(Comparator.comparing(TestPartQuestion::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(testPartQuestion -> {
                        Question question = testPartQuestion.getQuestion();
                        PracticeQuestionVM questionVM = new PracticeQuestionVM();
                        questionVM.setId(question.getId());
                        questionVM.setText(question.getPrompt() != null ? question.getPrompt() : "");
                        questionVM.setType(
                                question.getQuestionType() != null ? question.getQuestionType().getName() : "");
                        Integer displayOrder = testPartQuestion.getDisplayOrder();
                        questionVM.setOrder(displayOrder != null ? displayOrder : 0);
                        questionVM.setCorrect(java.util.Optional.empty());

                        // Process options
                        if (question.getOptions() != null) {
                            List<PracticeOptionVM> options = question.getOptions().stream()
                                    .filter(Objects::nonNull)
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
                        vm.setOrder(displayOrder != null ? displayOrder : 0);
                        vm.setQuestion(questionVM);
                        return vm;
                    })
                    .forEach(questionsAndSets::add);
        }

        // Process question sets in this test part
        if (testPart.getTestPartQuestionSets() != null) {
            testPart.getTestPartQuestionSets().stream()
                    .filter(Objects::nonNull)
                    .filter(tpqs -> tpqs.getQuestionSet() != null)
                    .sorted(Comparator.comparing(TestPartQuestionSet::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(testPartQuestionSet -> {
                        QuestionSet questionSet = testPartQuestionSet.getQuestionSet();
                        PracticeQuestionSetVM questionSetVM = new PracticeQuestionSetVM();
                        questionSetVM.setId(questionSet.getId());
                        questionSetVM.setTitle(questionSet.getTitle() != null ? questionSet.getTitle() : "");
                        Integer qsDisplayOrder = testPartQuestionSet.getDisplayOrder();
                        int orderVal = qsDisplayOrder != null ? qsDisplayOrder : 0;
                        questionSetVM.setOrder(orderVal);

                        // Process questions in this question set
                        if (questionSet.getQuestionSetItems() != null) {
                            List<PracticeQuestionVM> questions = questionSet.getQuestionSetItems().stream()
                                    .filter(Objects::nonNull)
                                    .filter(item -> Boolean.TRUE.equals(item.getIsActive()))
                                    .filter(item -> item.getQuestion() != null)
                                    .sorted(Comparator.comparing(QuestionSetItem::getOrder,
                                            Comparator.nullsLast(Comparator.naturalOrder())))
                                    .map(item -> {
                                        Question question = item.getQuestion();
                                        PracticeQuestionVM qVM = new PracticeQuestionVM();
                                        qVM.setId(question.getId());
                                        qVM.setText(question.getPrompt() != null ? question.getPrompt() : "");
                                        // Get the question type name from the QuestionType entity
                                        qVM.setType(question.getQuestionType() != null
                                                ? question.getQuestionType().getName()
                                                : "");
                                        Integer itemOrder = item.getOrder();
                                        qVM.setOrder(itemOrder != null ? itemOrder + orderVal - 1 : 0);
                                        qVM.setCorrect(java.util.Optional.empty());

                                        // Process options
                                        if (question.getOptions() != null) {
                                            List<PracticeOptionVM> options = question.getOptions().stream()
                                                    .filter(Objects::nonNull)
                                                    .map(opt -> new PracticeOptionVM(opt.getId(), opt.getText(),
                                                            opt.getOrder()))
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
                        vm.setOrder(orderVal);
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
    public PracticePartVM getPracticePartById(UUID partId, UUID testId, UUID testAttemptId) {
        // Get the test part that connects this part to the test
        TestPart testPart = partService.getTestPartByPartIdAndTestId(partId, testId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Test part not found for partId: " + partId + " and testId: " + testId));

        // Get the part with its relationships
        Part part = testPart.getPart();
        if (part == null) {
            throw new ResourceNotFoundException("Part not found for testPart: " + testPart.getId());
        }

        // Load all question responses for the given test attempt
        List<QuestionResponse> questionResponses = unitOfWork.getQuestionResponseRepository()
                .findByTestAttemptId(testAttemptId);

        // Create a map of question ID to correctness for quick lookup
        Map<UUID, Boolean> questionCorrectness = new HashMap<>();
        for (QuestionResponse response : questionResponses) {
            if (response.getQuestion() != null && response.getIsCorrect() != null) {
                questionCorrectness.put(response.getQuestion().getId(), response.getIsCorrect());
            }
        }

        // Create the view model
        PracticePartVM practicePartVM = new PracticePartVM();
        practicePartVM.setId(part.getId());
        practicePartVM.setName(part.getName() != null ? part.getName() : "");

        // Initialize the list to hold both questions and question sets
        List<PracticeQuestionAndQuestionSetVM> questionsAndSets = new ArrayList<>();

        // Process individual questions in this test part
        if (testPart.getTestPartQuestions() != null) {
            testPart.getTestPartQuestions().stream()
                    .filter(Objects::nonNull)
                    .filter(tpq -> tpq.getQuestion() != null)
                    .sorted(Comparator.comparing(TestPartQuestion::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(testPartQuestion -> {
                        Question question = testPartQuestion.getQuestion();
                        PracticeQuestionVM questionVM = new PracticeQuestionVM();
                        questionVM.setId(question.getId());
                        questionVM.setText(question.getPrompt() != null ? question.getPrompt() : "");
                        questionVM.setType(
                                question.getQuestionType() != null ? question.getQuestionType().getName() : "");
                        Integer displayOrder = testPartQuestion.getDisplayOrder();
                        questionVM.setOrder(displayOrder != null ? displayOrder : 0);

                        // Set correctness from attempt data if available
                        Boolean isCorrect = questionCorrectness.get(question.getId());
                        questionVM.setCorrect(java.util.Optional.ofNullable(isCorrect));

                        // Process options
                        if (question.getOptions() != null) {
                            List<PracticeOptionVM> options = question.getOptions().stream()
                                    .filter(Objects::nonNull)
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
                        vm.setOrder(displayOrder != null ? displayOrder : 0);
                        vm.setQuestion(questionVM);
                        return vm;
                    })
                    .forEach(questionsAndSets::add);
        }

        // Process question sets in this test part
        if (testPart.getTestPartQuestionSets() != null) {
            testPart.getTestPartQuestionSets().stream()
                    .filter(Objects::nonNull)
                    .filter(tpqs -> tpqs.getQuestionSet() != null)
                    .sorted(Comparator.comparing(TestPartQuestionSet::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(testPartQuestionSet -> {
                        QuestionSet questionSet = testPartQuestionSet.getQuestionSet();
                        PracticeQuestionSetVM questionSetVM = new PracticeQuestionSetVM();
                        questionSetVM.setId(questionSet.getId());
                        questionSetVM.setTitle(questionSet.getTitle() != null ? questionSet.getTitle() : "");
                        Integer qsDisplayOrder = testPartQuestionSet.getDisplayOrder();
                        int orderVal = qsDisplayOrder != null ? qsDisplayOrder : 0;
                        questionSetVM.setOrder(orderVal);

                        // Process questions in this question set
                        if (questionSet.getQuestionSetItems() != null) {
                            List<PracticeQuestionVM> questions = questionSet.getQuestionSetItems().stream()
                                    .filter(Objects::nonNull)
                                    .filter(item -> Boolean.TRUE.equals(item.getIsActive()))
                                    .filter(item -> item.getQuestion() != null)
                                    .sorted(Comparator.comparing(QuestionSetItem::getOrder,
                                            Comparator.nullsLast(Comparator.naturalOrder())))
                                    .map(item -> {
                                        Question question = item.getQuestion();
                                        PracticeQuestionVM qVM = new PracticeQuestionVM();
                                        qVM.setId(question.getId());
                                        qVM.setText(question.getPrompt() != null ? question.getPrompt() : "");
                                        // Get the question type name from the QuestionType entity
                                        qVM.setType(question.getQuestionType() != null
                                                ? question.getQuestionType().getName()
                                                : "");
                                        Integer itemOrder = item.getOrder();
                                        qVM.setOrder(itemOrder != null ? itemOrder + orderVal - 1 : 0);

                                        // Set correctness from attempt data if available
                                        Boolean isCorrect = questionCorrectness.get(question.getId());
                                        qVM.setCorrect(java.util.Optional.ofNullable(isCorrect));

                                        // Process options
                                        if (question.getOptions() != null) {
                                            List<PracticeOptionVM> options = question.getOptions().stream()
                                                    .filter(Objects::nonNull)
                                                    .map(opt -> new PracticeOptionVM(opt.getId(), opt.getText(),
                                                            opt.getOrder()))
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
                        vm.setOrder(orderVal);
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
                    .filter(Objects::nonNull)
                    .filter(tp -> tp.getPart() != null)
                    .filter(tp -> partIds == null || partIds.isEmpty() ||
                            partIds.contains(tp.getPart().getId()))
                    .sorted(Comparator.comparing(TestPart::getOrderIndex,
                            Comparator.nullsLast(Comparator.naturalOrder())))
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
                    .filter(Objects::nonNull)
                    .filter(detail -> detail.getQuestion() != null)
                    .sorted(Comparator.comparing(TestQuestionDetail::getOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(detail -> {
                        Question question = detail.getQuestion();
                        int order = detail.getOrder() != null ? detail.getOrder() : 0;
                        PracticeQuestionVM questionVM = mapQuestionToVM(question, order, null);

                        PracticeQuestionAndQuestionSetVM itemVM = new PracticeQuestionAndQuestionSetVM();
                        itemVM.setId(question.getId());
                        itemVM.setOrder(order);
                        itemVM.setQuestion(questionVM);
                        return itemVM;
                    })
                    .forEach(allQuestionItems::add);
        }

        // Map question sets from testQuestionSetDetails
        if (test.getTestQuestionSetDetails() != null) {
            test.getTestQuestionSetDetails().stream()
                    .filter(Objects::nonNull)
                    .filter(detail -> detail.getQuestionSet() != null)
                    .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(detail -> {
                        QuestionSet questionSet = detail.getQuestionSet();
                        int order = detail.getOrder() != null ? detail.getOrder() : 0;
                        PracticeQuestionSetVM setVM = mapQuestionSetToVM(questionSet, order);

                        PracticeQuestionAndQuestionSetVM itemVM = new PracticeQuestionAndQuestionSetVM();
                        itemVM.setId(questionSet.getId());
                        itemVM.setOrder(order);
                        itemVM.setQuestionSet(setVM);
                        return itemVM;
                    })
                    .forEach(allQuestionItems::add);
        }

        // Sort all items by their order
        allQuestionItems.sort(Comparator.comparingInt(PracticeQuestionAndQuestionSetVM::getOrder));

        // Get audio files from test
        List<PracticeFileInfoVM> audioFiles = getTestAudioFiles(test);

        vm.setParts(parts);
        vm.setQuestionAndQuestionSet(allQuestionItems);
        vm.setAudioFiles(audioFiles);
        return vm;
    }

    @Override
    public PracticeTestVM getPracticeTestByParts(UUID testId, Set<UUID> partIds, UUID testAttemptId) {
        // Get the test with its relationships
        Test test = unitOfWork.getTestRepository().findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + testId));

        // Load all question responses for the given test attempt
        List<QuestionResponse> questionResponses = unitOfWork.getQuestionResponseRepository()
                .findByTestAttemptId(testAttemptId);

        // Create a map of question ID to correctness for quick lookup
        Map<UUID, Boolean> questionCorrectness = new HashMap<>();
        for (QuestionResponse response : questionResponses) {
            if (response.getQuestion() != null && response.getIsCorrect() != null) {
                questionCorrectness.put(response.getQuestion().getId(), response.getIsCorrect());
            }
        }

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
                    .filter(Objects::nonNull)
                    .filter(tp -> tp.getPart() != null)
                    .filter(tp -> partIds == null || partIds.isEmpty() ||
                            partIds.contains(tp.getPart().getId()))
                    .sorted(Comparator.comparing(TestPart::getOrderIndex,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());

            // Process each test part to get its details and questions/question sets
            for (TestPart testPart : testParts) {
                UUID partId = testPart.getPart().getId();
                PracticePartVM partVM = getPracticePartById(partId, testId, testAttemptId);

                if (partVM != null) {
                    parts.add(partVM);
                }
            }
        }

        // Map individual questions from testQuestionDetails
        if (test.getTestQuestionDetails() != null) {
            test.getTestQuestionDetails().stream()
                    .filter(Objects::nonNull)
                    .filter(detail -> detail.getQuestion() != null)
                    .sorted(Comparator.comparing(TestQuestionDetail::getOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(detail -> {
                        Question question = detail.getQuestion();
                        int order = detail.getOrder() != null ? detail.getOrder() : 0;
                        PracticeQuestionVM questionVM = mapQuestionToVM(question, order, null);

                        // Set correctness from attempt data if available
                        Boolean isCorrect = questionCorrectness.get(question.getId());
                        questionVM.setCorrect(java.util.Optional.ofNullable(isCorrect));

                        PracticeQuestionAndQuestionSetVM itemVM = new PracticeQuestionAndQuestionSetVM();
                        itemVM.setId(question.getId());
                        itemVM.setOrder(order);
                        itemVM.setQuestion(questionVM);
                        return itemVM;
                    })
                    .forEach(allQuestionItems::add);
        }

        // Map question sets from testQuestionSetDetails
        if (test.getTestQuestionSetDetails() != null) {
            test.getTestQuestionSetDetails().stream()
                    .filter(Objects::nonNull)
                    .filter(detail -> detail.getQuestionSet() != null)
                    .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(detail -> {
                        QuestionSet questionSet = detail.getQuestionSet();
                        int order = detail.getOrder() != null ? detail.getOrder() : 0;
                        PracticeQuestionSetVM setVM = mapQuestionSetToVM(questionSet, order);

                        // Update questions in the set with correctness information
                        if (setVM.getQuestions() != null) {
                            setVM.getQuestions().forEach(q -> {
                                Boolean isCorrect = questionCorrectness.get(q.getId());
                                q.setCorrect(java.util.Optional.ofNullable(isCorrect));
                            });
                        }

                        PracticeQuestionAndQuestionSetVM itemVM = new PracticeQuestionAndQuestionSetVM();
                        itemVM.setId(questionSet.getId());
                        itemVM.setOrder(order);
                        itemVM.setQuestionSet(setVM);
                        return itemVM;
                    })
                    .forEach(allQuestionItems::add);
        }

        // Sort all items by their order
        allQuestionItems.sort(Comparator.comparingInt(PracticeQuestionAndQuestionSetVM::getOrder));

        // Get audio files from test
        List<PracticeFileInfoVM> audioFiles = getTestAudioFiles(test);

        vm.setParts(parts);
        vm.setQuestionAndQuestionSet(allQuestionItems);
        vm.setAudioFiles(audioFiles);
        return vm;
    }

    /**
     * Get audio files from test
     * 
     * @param test The test entity
     * @return List of PracticeFileInfoVM representing audio files
     */
    private List<PracticeFileInfoVM> getTestAudioFiles(Test test) {
        if (test == null || test.getTestFiles() == null || test.getTestFiles().isEmpty()) {
            return Collections.emptyList();
        }

        return test.getTestFiles().stream()
                .map(TestFile::getFile)
                .filter(Objects::nonNull)
                .map(this::mapFileInfoToVM)
                .collect(Collectors.toList());
    }
}
