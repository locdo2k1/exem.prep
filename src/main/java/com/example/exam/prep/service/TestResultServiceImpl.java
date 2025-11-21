package com.example.exam.prep.service;

import com.example.exam.prep.model.*;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.example.exam.prep.vm.testresult.AnalysisPartVM;
import com.example.exam.prep.vm.testresult.AnalysisQuestionsVM;
import com.example.exam.prep.vm.testresult.AnswerResultVM;
import com.example.exam.prep.vm.testresult.QuestionResultVM;
import com.example.exam.prep.vm.testresult.QuestionResultDTO;
import com.example.exam.prep.vm.testresult.OptionResultVM;
import com.example.exam.prep.vm.testresult.PartResultVM;
import com.example.exam.prep.vm.testresult.TestResultOverallVM;
import com.example.exam.prep.vm.testresult.TestInfoVM;
import com.example.exam.prep.vm.testresult.AnalysisQuesCategory;
import com.example.exam.prep.vm.testresult.FileInfoResultVM;
import com.example.exam.prep.vm.PartViewModel;

import java.util.stream.Stream;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.exam.prep.util.TimeFormatHelper;

/**
 * Service implementation for handling test result related operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultServiceImpl implements ITestResultService {

        private final IUnitOfWork unitOfWork;
        private final IFileStorageService fileStorageService;
        private List<QuestionResponse> questionResponses;

        @Override
        public TestResultOverallVM getTestResultOverall(UUID attemptId) {
                List<TestPartAttempt> testPartAttempts = unitOfWork.getTestPartAttemptRepository()
                                .findByTestAttemptId(attemptId);

                TestAttempt testAttempt = unitOfWork.getTestAttemptRepository().findById(attemptId)
                                .orElseThrow(() -> new RuntimeException("Test attempt not found for ID: " + attemptId));

                List<QuestionResponse> questionResponses = unitOfWork.getQuestionResponseRepository()
                                .findByTestAttemptId(attemptId);

                int totalAnsweredQuestion = questionResponses.size();

                List<QuestionResponse> correctQuestions = questionResponses.stream()
                                .filter(QuestionResponse::getIsCorrect)
                                .toList();

                int correctQuestionsCount = correctQuestions.size();
                int incorrectQuestions = questionResponses.size() - correctQuestionsCount;
                double totalScore = correctQuestions.stream()
                                .mapToDouble(QuestionResponse::getScore)
                                .sum();

                int totalQuestions = calculateTotalQuestions(testPartAttempts, testAttempt);
                List<PartViewModel> partsList = buildPartsList(testPartAttempts);

                return TestResultOverallVM.builder()
                                .totalQuestions(totalQuestions)
                                .correctAnswers(correctQuestionsCount)
                                .incorrectAnswers(incorrectQuestions)
                                .skippedQuestions(totalQuestions - correctQuestionsCount - incorrectQuestions)
                                .accuracyPercentage(totalAnsweredQuestion == 0 ? 0
                                                : Math.round(((double) correctQuestionsCount / totalAnsweredQuestion)
                                                                * 10000.0) / 100.0)
                                .score(totalScore)
                                .completionTime(TimeFormatHelper.formatSecondsToHms(testAttempt.getDurationSeconds()))
                                .parts(partsList)
                                .build();
        }

        @Override
        public AnalysisQuestionsVM getTestAttemptAnalysis(UUID attemptId) {
                TestAttempt testAttempt = unitOfWork.getTestAttemptRepository().findById(attemptId)
                                .orElseThrow(() -> new RuntimeException("Test attempt not found for ID: " + attemptId));

                questionResponses = unitOfWork.getQuestionResponseRepository().findByTestAttemptId(attemptId);

                // Get all questions in the test
                List<QuestionResultDTO> allQuestions = getAllQuestionsInTest(testAttempt.getTest().getId());

                // Get attempted part IDs
                List<UUID> attemptedPartIds = getAttemptedPartIds(attemptId);

                // Convert all questions to analysis categories
                List<AnalysisQuesCategory> allAnalysisCategories = convertToAnalysisByCategory(allQuestions);

                // Filter categories to only include attempted parts or categories without a
                // part
                List<AnalysisQuesCategory> filteredCategories = filterAttemptedCategories(allAnalysisCategories,
                                attemptedPartIds);

                // Separate general questions (without partId) from part-specific questions
                Map<Boolean, List<AnalysisQuesCategory>> partitionedCategories = partitionedCategories(
                                filteredCategories);

                // Get general questions for overall analysis
                List<AnalysisQuesCategory> overallCategories = partitionedCategories.get(false);

                // Create part analysis for each part
                List<AnalysisPartVM> parts = buildPartAnalysis(partitionedCategories.get(true), testAttempt);

                return AnalysisQuestionsVM.builder()
                                .parts(parts.stream()
                                                .sorted(Comparator.comparing(AnalysisPartVM::getOrder))
                                                .collect(Collectors.toList()))
                                .overall(overallCategories)
                                .build();
        }

        @Override
        public AnswerResultVM getTestAnswers(UUID attemptId) {
                TestAttempt testAttempt = unitOfWork.getTestAttemptRepository().findById(attemptId).orElseThrow(
                                () -> new RuntimeException(
                                                "Test attempt not found for ID: " + attemptId));

                // Get all question responses for this attempt
                List<QuestionResponse> questionResponses = unitOfWork.getQuestionResponseRepository()
                                .findByTestAttemptId(attemptId);

                // Create maps for quick lookup
                QuestionResponseMaps responseMaps = buildQuestionResponseMaps(questionResponses);

                List<TestPartAttempt> testPartAttempts = unitOfWork.getTestPartAttemptRepository()
                                .findByTestAttemptId(attemptId);

                List<Part> parts = testPartAttempts.stream()
                                .map(TestPartAttempt::getPart)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                List<PartResultVM> partResults = buildPartResults(parts, testAttempt, responseMaps);

                // Get all questions across all parts for the overall list
                List<QuestionResultVM> allQuestions = buildOverallQuestionsList(testAttempt.getTest(), responseMaps);

                // Sort parts by order and questions within each part
                List<PartResultVM> sortedParts = partResults.stream()
                                .peek(part -> {
                                        if (part.getQuestions() != null) {
                                                part.getQuestions()
                                                                .sort(Comparator.comparing(QuestionResultVM::getOrder));
                                        }
                                })
                                .sorted(Comparator.comparing(PartResultVM::getOrder,
                                                Comparator.nullsLast(Comparator.naturalOrder())))
                                .collect(Collectors.toList());

                // Build the final result
                return AnswerResultVM.builder()
                                .parts(sortedParts)
                                .overall(allQuestions)
                                .build();
        }

        /**
         * Converts a list of QuestionResultDTO to a list of AnalysisQuesCategory
         * grouped by question categories
         *
         * @param questionDTOs List of QuestionResultDTO to convert
         * @return List of AnalysisQuesCategory grouped by category
         */
        public List<AnalysisQuesCategory> convertToAnalysisByCategory(List<QuestionResultDTO> questionDTOs) {
                if (questionDTOs == null || questionDTOs.isEmpty()) {
                        return Collections.emptyList();
                }

                // Group questions by their categories and partId
                Map<String, Map<Optional<UUID>, List<QuestionResultVM>>> categoryPartMap = groupQuestionsByCategoryAndPart(
                                questionDTOs);

                // Create AnalysisQuesCategory for each category and partId combination
                List<AnalysisQuesCategory> result = buildAnalysisCategoriesFromMap(categoryPartMap);

                // Sort categories by name for consistent ordering
                result.sort(Comparator.comparing(AnalysisQuesCategory::getCategoryName));

                return result;
        }

        /**
         * Retrieves all questions from a test, including those in question sets and
         * test parts, mapped to QuestionResultDTO
         *
         * @param testId The ID of the test
         * @return List of all questions in the test as QuestionResultDTO
         */
        @Override
        public TestInfoVM getTestInfo(UUID attemptId) {
                TestAttempt testAttempt = unitOfWork.getTestAttemptRepository().findById(attemptId)
                                .orElseThrow(() -> new RuntimeException("Test attempt not found for ID: " + attemptId));

                // Get test from the attempt
                Test test = testAttempt.getTest();
                if (test == null) {
                        throw new RuntimeException("Test not found for attempt ID: " + attemptId);
                }

                // Get part names from test part attempts
                List<TestPartAttempt> testPartAttempts = unitOfWork.getTestPartAttemptRepository()
                                .findByTestAttemptId(attemptId);

                List<String> partNames = new ArrayList<>();

                if (testPartAttempts != null && !testPartAttempts.isEmpty()) {
                        List<TestPart> parts = unitOfWork.getTestPartRepository()
                                        .findByTestIdWithParts(testAttempt.getTest().getId());

                        // Create a map of part names to their order in the parts list
                        Map<String, Integer> partNameOrder = new HashMap<>();
                        for (int i = 0; i < parts.size(); i++) {
                                partNameOrder.put(parts.get(i).getPart().getName(), i);
                        }

                        // Extract and sort part names based on the order in parts
                        partNames = testPartAttempts.stream()
                                        .map(TestPartAttempt::getPart)
                                        .filter(Objects::nonNull)
                                        .map(Part::getName)
                                        .filter(Objects::nonNull)
                                        .sorted(Comparator.comparing(
                                                        partName -> partNameOrder.getOrDefault(partName,
                                                                        Integer.MAX_VALUE)))
                                        .collect(Collectors.toList());
                }

                return TestInfoVM.builder()
                                .testId(test.getId())
                                .testName(test.getName())
                                .partNames(partNames)
                                .build();
        }

        /**
         * Maps a FileInfo entity to a FileInfoResultVM view model
         */
        private FileInfoResultVM mapFileInfoToVM(FileInfo fileInfo) {
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

                return new FileInfoResultVM(
                                fileInfo.getId(),
                                fileInfo.getFileName(),
                                fileUrl,
                                fileInfo.getFileType(),
                                fileInfo.getFileSize());
        }

        @Override
        public List<QuestionResultDTO> getAllQuestionsInTest(UUID testId) {
                List<QuestionResultDTO> allQuestionDTOs = new ArrayList<>();
                List<TestPart> testParts = unitOfWork.getTestPartRepository().findByTestIdWithParts(testId);
                Map<UUID, TestPart> questionToPartMap = new HashMap<>();

                // Build a map of question ID to its containing part (for parts and part
                // question sets)
                testParts.forEach(part -> {
                        // Map questions from TestPartQuestion
                        part.getTestPartQuestions().forEach(tpq -> {
                                if (tpq.getQuestion() != null) {
                                        questionToPartMap.put(tpq.getQuestion().getId(), part);
                                }
                        });

                        // Map questions from TestPartQuestionSet
                        part.getTestPartQuestionSets().forEach(partSet -> {
                                partSet.getQuestionSet().getQuestionSetItems().forEach(item -> {
                                        if (item.getQuestion() != null) {
                                                questionToPartMap.put(item.getQuestion().getId(), part);
                                        }
                                });
                        });
                });

                // 1. Get direct questions from TestQuestionDetail (no part)
                List<TestQuestionDetail> questionDetails = unitOfWork.getTestQuestionDetailRepository()
                                .findByTestId(testId);
                List<QuestionResultDTO> directQuestions = questionDetails.stream()
                                .map(detail -> mapQuestionToDTO(detail.getQuestion(), detail.getOrder(), null))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                allQuestionDTOs.addAll(directQuestions);

                // 2. Get questions from TestQuestionSetDetail (no part)
                List<TestQuestionSetDetail> questionSetDetails = unitOfWork.getTestQuestionSetDetailRepository()
                                .findByTestId(testId);
                List<QuestionResultDTO> questionsFromSets = questionSetDetails.stream()
                                .flatMap(detail -> detail.getQuestionSet().getQuestionSetItems().stream()
                                                .map(item -> mapQuestionToDTO(item.getQuestion(),
                                                                detail.getOrder() - 1 + item.getOrder(),
                                                                null)))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                allQuestionDTOs.addAll(questionsFromSets);

                // 3. Get questions from TestPartQuestion (with part)
                List<QuestionResultDTO> partQuestions = testParts.stream()
                                .flatMap(part -> part.getTestPartQuestions().stream()
                                                .map(tpq -> mapQuestionToDTO(tpq.getQuestion(), tpq.getDisplayOrder(),
                                                                part)))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                allQuestionDTOs.addAll(partQuestions);

                // 4. Get questions from TestPartQuestionSet (with part)
                List<QuestionResultDTO> partSetQuestions = testParts.stream()
                                .flatMap(part -> part.getTestPartQuestionSets().stream()
                                                .flatMap(partSet -> partSet.getQuestionSet().getQuestionSetItems()
                                                                .stream()
                                                                .map(item -> mapQuestionToDTO(item.getQuestion(),
                                                                                item.getOrder() - 1 + item.getOrder(),
                                                                                part))))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                allQuestionDTOs.addAll(partSetQuestions);

                return allQuestionDTOs;
        }

        /**
         * Maps a Question entity to a QuestionResultDTO
         *
         * @param question The question to map
         * @param order    The display order of the question
         * @param part     The part this question belongs to (can be null)
         * @return Mapped QuestionResultDTO
         */
        private QuestionResultDTO mapQuestionToDTO(Question question, Integer order, TestPart part) {
                if (question == null) {
                        return null;
                }

                // Find the corresponding question response if it exists
                QuestionResponse questionResponse = questionResponses != null ? questionResponses.stream()
                                .filter(response -> response.getQuestion().getId().equals(question.getId()))
                                .findFirst()
                                .orElse(null) : null;

                // Get selected option IDs from the response
                Set<UUID> selectedOptionIds = questionResponse != null ? questionResponse.getSelectedOptions().stream()
                                .map(selectedOption -> selectedOption.getOption().getId())
                                .collect(Collectors.toSet())
                                : Collections.emptySet();

                return QuestionResultDTO.builder()
                                .PartId(part != null ? Optional.of(part.getPart().getId()) : Optional.empty())
                                .order(order != null ? order : 0)
                                .context(question.getPrompt())
                                .explanation(null) // No explanation in Question entity
                                .transcript(question.getTranscript()) // Using transcript from Question entity
                                .questionType(question.getQuestionType() != null ? question.getQuestionType().getCode()
                                                : null)
                                .isCorrect(questionResponse != null ? questionResponse.getIsCorrect() : null)
                                .correctOptions(question.getOptions() != null ? question.getOptions().stream()
                                                .filter(Option::isCorrect)
                                                .map(option -> OptionResultVM.builder()
                                                                .id(option.getId().toString())
                                                                .text(option.getText())
                                                                .isCorrect(true)
                                                                .build())
                                                .collect(Collectors.toList()) : Collections.emptyList())
                                .correctAnswers(question.getFillBlankAnswers() != null
                                                ? question.getFillBlankAnswers().stream()
                                                                .map(FillBlankAnswer::getAnswerText)
                                                                .collect(Collectors.toList())
                                                : Collections.emptyList())
                                .userAnswer(questionResponse != null ? questionResponse.getTextAnswer() : null)
                                .options(question.getOptions() != null ? question.getOptions().stream()
                                                .map(option -> OptionResultVM.builder()
                                                                .id(option.getId().toString())
                                                                .text(option.getText())
                                                                .isCorrect(option.isCorrect())
                                                                .isSelected(selectedOptionIds.contains(option.getId()))
                                                                .build())
                                                .collect(Collectors.toList()) : Collections.emptyList())
                                .questionCategories(question.getCategory() != null
                                                ? List.of(question.getCategory().getName())
                                                : Collections.emptyList())
                                .build();
        }

        // ============= Helper Methods to Reduce Nesting =============

        private int calculateTotalQuestions(List<TestPartAttempt> testPartAttempts, TestAttempt testAttempt) {
                if (!testPartAttempts.isEmpty()) {
                        return testPartAttempts.stream()
                                        .mapToInt(testPartAttempt -> countQuestionsInTestPart(testPartAttempt,
                                                        testAttempt))
                                        .sum();
                }
                return countQuestionsDirectly(testAttempt.getTest().getId());
        }

        private int countQuestionsInTestPart(TestPartAttempt testPartAttempt, TestAttempt testAttempt) {
                Part part = testPartAttempt.getPart();
                TestPart testPart = part.getTestParts().stream()
                                .filter(tp -> tp.getTest().getId().equals(testAttempt.getTest().getId()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException(
                                                "Test part not found for test ID: " + testAttempt.getTest().getId()));

                int directQuestions = testPart.getTestPartQuestions() != null ? testPart.getTestPartQuestions().size()
                                : 0;
                int questionsInSets = countQuestionsInQuestionSets(testPart.getTestPartQuestionSets());
                return directQuestions + questionsInSets;
        }

        private int countQuestionsInQuestionSets(Set<TestPartQuestionSet> questionSets) {
                if (questionSets == null) {
                        return 0;
                }
                return questionSets.stream()
                                .filter(qs -> qs.getQuestionSet() != null
                                                && qs.getQuestionSet().getQuestionSetItems() != null)
                                .mapToInt(qs -> qs.getQuestionSet().getQuestionSetItems().size())
                                .sum();
        }

        private int countQuestionsDirectly(UUID testId) {
                List<TestQuestionDetail> testQuestionDetails = unitOfWork.getTestQuestionDetailRepository()
                                .findByTestId(testId);
                List<TestQuestionSetDetail> testQuestionSetDetails = unitOfWork.getTestQuestionSetDetailRepository()
                                .findByTestId(testId);

                int directQuestions = testQuestionDetails != null ? testQuestionDetails.size() : 0;
                int questionsInSets = 0;

                if (testQuestionSetDetails != null) {
                        for (TestQuestionSetDetail setDetail : testQuestionSetDetails) {
                                if (setDetail.getQuestionSet() != null
                                                && setDetail.getQuestionSet().getQuestionSetItems() != null) {
                                        questionsInSets += setDetail.getQuestionSet().getQuestionSetItems().size();
                                }
                        }
                }
                return directQuestions + questionsInSets;
        }

        private List<PartViewModel> buildPartsList(List<TestPartAttempt> testPartAttempts) {
                return testPartAttempts.stream()
                                .map(TestPartAttempt::getPart)
                                .filter(Objects::nonNull)
                                .map(PartViewModel::fromModel)
                                .collect(Collectors.toList());
        }

        private List<UUID> getAttemptedPartIds(UUID attemptId) {
                return unitOfWork.getTestPartAttemptRepository()
                                .findByTestAttemptId(attemptId)
                                .stream()
                                .map(testPartAttempt -> testPartAttempt.getPart().getId())
                                .collect(Collectors.toList());
        }

        private List<AnalysisQuesCategory> filterAttemptedCategories(List<AnalysisQuesCategory> categories,
                        List<UUID> attemptedPartIds) {
                return categories.stream()
                                .filter(category -> !category.getPartId().isPresent() ||
                                                (category.getPartId().isPresent() && attemptedPartIds
                                                                .contains(category.getPartId().get())))
                                .collect(Collectors.toList());
        }

        private Map<Boolean, List<AnalysisQuesCategory>> partitionedCategories(List<AnalysisQuesCategory> categories) {
                return categories.stream()
                                .collect(Collectors.partitioningBy(cat -> cat.getPartId().isPresent()));
        }

        private List<AnalysisPartVM> buildPartAnalysis(List<AnalysisQuesCategory> partCategories,
                        TestAttempt testAttempt) {
                Map<Optional<UUID>, List<AnalysisQuesCategory>> categoriesByPart = partCategories.stream()
                                .collect(Collectors.groupingBy(AnalysisQuesCategory::getPartId));

                return categoriesByPart.entrySet().stream()
                                .map(entry -> createAnalysisPartVM(entry.getKey().orElse(null), entry.getValue(),
                                                testAttempt))
                                .sorted(Comparator.comparing(AnalysisPartVM::getOrder))
                                .collect(Collectors.toList());
        }

        private AnalysisPartVM createAnalysisPartVM(UUID partId, List<AnalysisQuesCategory> categories,
                        TestAttempt testAttempt) {
                AnalysisPartVM partVM = new AnalysisPartVM();
                Part part = unitOfWork.getPartRepository().findById(partId).orElse(null);
                TestPart testPart = unitOfWork.getTestPartRepository()
                                .findByPartIdAndTestId(partId, testAttempt.getTest().getId())
                                .orElse(null);

                if (part != null) {
                        partVM.setPartName(part.getName());
                }
                if (testPart != null) {
                        partVM.setOrder(testPart.getOrderIndex());
                }
                partVM.setCategories(categories);
                return partVM;
        }

        private QuestionResponseMaps buildQuestionResponseMaps(List<QuestionResponse> questionResponses) {
                Map<UUID, Set<UUID>> selectedOptionIds = new HashMap<>();
                Map<UUID, String> textAnswers = new HashMap<>();
                Map<UUID, Boolean> correctness = new HashMap<>();

                for (QuestionResponse response : questionResponses) {
                        if (response.getQuestion() == null) {
                                continue;
                        }
                        UUID questionId = response.getQuestion().getId();

                        selectedOptionIds.put(questionId,
                                        response.getSelectedOptions().stream()
                                                        .map(opt -> opt.getOption().getId())
                                                        .collect(Collectors.toSet()));

                        if (response.getTextAnswer() != null && !response.getTextAnswer().isEmpty()) {
                                textAnswers.put(questionId, response.getTextAnswer());
                        }

                        if (response.getIsCorrect() != null) {
                                correctness.put(questionId, response.getIsCorrect());
                        }
                }
                return new QuestionResponseMaps(selectedOptionIds, textAnswers, correctness);
        }

        private List<PartResultVM> buildPartResults(List<Part> parts, TestAttempt testAttempt,
                        QuestionResponseMaps responseMaps) {
                return parts.stream()
                                .map(part -> buildPartResultVM(part, testAttempt, responseMaps))
                                .sorted(Comparator.comparing(PartResultVM::getOrder,
                                                Comparator.nullsLast(Comparator.naturalOrder())))
                                .collect(Collectors.toList());
        }

        private PartResultVM buildPartResultVM(Part part, TestAttempt testAttempt, QuestionResponseMaps responseMaps) {
                UUID partId = part.getId();
                UUID testId = testAttempt.getTest().getId();

                List<QuestionResultVM> questionResults = getQuestionsForPart(partId, testId, responseMaps);
                PartInfo partInfo = getPartInfo(partId, testId);

                return PartResultVM.builder()
                                .partId(partId.toString())
                                .name(partInfo.name)
                                .order(partInfo.order)
                                .questions(questionResults.stream()
                                                .sorted(Comparator.comparing(QuestionResultVM::getOrder))
                                                .collect(Collectors.toList()))
                                .build();
        }

        private List<QuestionResultVM> getQuestionsForPart(UUID partId, UUID testId,
                        QuestionResponseMaps responseMaps) {
                return unitOfWork.getTestPartRepository()
                                .findByPartIdAndTestId(partId, testId)
                                .stream()
                                .flatMap(testPart -> getQuestionsFromTestPart(testPart, responseMaps))
                                .collect(Collectors.toList());
        }

        private Stream<QuestionResultVM> getQuestionsFromTestPart(TestPart testPart,
                        QuestionResponseMaps responseMaps) {
                List<QuestionWithQuestionSetInfo> questionsWithInfo = new ArrayList<>();

                collectDirectQuestions(testPart, questionsWithInfo);
                collectQuestionSetQuestions(testPart, questionsWithInfo);

                return questionsWithInfo.stream()
                                .map(qinfo -> mapQuestionWithInfoToVM(qinfo, responseMaps))
                                .filter(Objects::nonNull);
        }

        private void collectDirectQuestions(TestPart testPart, List<QuestionWithQuestionSetInfo> questionsWithInfo) {
                if (testPart.getTestPartQuestions() != null) {
                        testPart.getTestPartQuestions().stream()
                                        .filter(Objects::nonNull)
                                        .forEach(tpq -> {
                                                if (tpq != null && tpq.getQuestion() != null) {
                                                        questionsWithInfo.add(new QuestionWithQuestionSetInfo(
                                                                        tpq.getQuestion(), null));
                                                }
                                        });
                }
        }

        private void collectQuestionSetQuestions(TestPart testPart,
                        List<QuestionWithQuestionSetInfo> questionsWithInfo) {
                if (testPart.getTestPartQuestionSets() != null) {
                        testPart.getTestPartQuestionSets().stream()
                                        .filter(Objects::nonNull)
                                        .forEach(tpqs -> {
                                                if (tpqs.getQuestionSet() != null && tpqs.getQuestionSet()
                                                                .getQuestionSetItems() != null) {
                                                        String description = tpqs.getQuestionSet().getDescription();
                                                        tpqs.getQuestionSet().getQuestionSetItems().stream()
                                                                        .filter(Objects::nonNull)
                                                                        .forEach(item -> {
                                                                                if (item.getQuestion() != null) {
                                                                                        questionsWithInfo.add(
                                                                                                        new QuestionWithQuestionSetInfo(
                                                                                                                        item.getQuestion(),
                                                                                                                        description));
                                                                                }
                                                                        });
                                                }
                                        });
                }
        }

        private QuestionResultVM mapQuestionWithInfoToVM(QuestionWithQuestionSetInfo qinfo,
                        QuestionResponseMaps responseMaps) {
                Question question = qinfo.question;
                if (question == null) {
                        return null;
                }

                QuestionResultVM.QuestionResultVMBuilder builder = QuestionResultVM.builder()
                                .order(question.getOrder() != null ? question.getOrder() : 0)
                                .context(question.getPrompt() != null ? question.getPrompt() : "")
                                .outerContent(qinfo.questionSetDescription)
                                .questionType(question.getQuestionType() != null ? question.getQuestionType().getName()
                                                : null)
                                .transcript(question.getTranscript());

                addOptionsToBuilder(builder, question, responseMaps);
                addFillBlankAnswersToBuilder(builder, question);
                addUserAnswerToBuilder(builder, question, responseMaps);
                addFileInfosToBuilder(builder, question);

                return builder.build();
        }

        private void addOptionsToBuilder(QuestionResultVM.QuestionResultVMBuilder builder, Question question,
                        QuestionResponseMaps responseMaps) {
                if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                        Set<UUID> selectedOptionIds = responseMaps.selectedOptionIds.getOrDefault(question.getId(),
                                        Collections.emptySet());

                        List<OptionResultVM> options = question.getOptions().stream()
                                        .map(option -> OptionResultVM.builder()
                                                        .id(option.getId().toString())
                                                        .text(option.getText())
                                                        .isCorrect(option.isCorrect())
                                                        .isSelected(selectedOptionIds.contains(option.getId()))
                                                        .build())
                                        .collect(Collectors.toList());

                        List<OptionResultVM> correctOptions = question.getOptions().stream()
                                        .filter(Option::isCorrect)
                                        .map(option -> OptionResultVM.builder()
                                                        .id(option.getId().toString())
                                                        .text(option.getText())
                                                        .isCorrect(true)
                                                        .build())
                                        .collect(Collectors.toList());

                        builder.options(options).correctOptions(correctOptions);
                }
        }

        private void addFillBlankAnswersToBuilder(QuestionResultVM.QuestionResultVMBuilder builder, Question question) {
                if (question.getFillBlankAnswers() != null && !question.getFillBlankAnswers().isEmpty()) {
                        List<String> correctAnswers = question.getFillBlankAnswers().stream()
                                        .map(FillBlankAnswer::getAnswerText)
                                        .collect(Collectors.toList());
                        builder.correctAnswers(correctAnswers);
                }
        }

        private void addUserAnswerToBuilder(QuestionResultVM.QuestionResultVMBuilder builder, Question question,
                        QuestionResponseMaps responseMaps) {
                if (question.getId() != null) {
                        builder.userAnswer(responseMaps.textAnswers.get(question.getId()))
                                        .isCorrect(responseMaps.correctness.get(question.getId()));
                }
        }

        private void addFileInfosToBuilder(QuestionResultVM.QuestionResultVMBuilder builder, Question question) {
                if (question.getFileInfos() != null && !question.getFileInfos().isEmpty()) {
                        List<FileInfoResultVM> fileInfos = question.getFileInfos().stream()
                                        .map(this::mapFileInfoToVM)
                                        .collect(Collectors.toList());
                        builder.questionAudios(fileInfos);
                }
        }

        private PartInfo getPartInfo(UUID partId, UUID testId) {
                String partName = "Part " + partId.toString();
                Integer partOrder = null;

                Optional<TestPart> testPartOpt = unitOfWork.getTestPartRepository()
                                .findByPartIdAndTestId(partId, testId)
                                .stream()
                                .findFirst();

                if (testPartOpt.isPresent()) {
                        TestPart testPart = testPartOpt.get();
                        partOrder = testPart.getOrderIndex();
                        Part partEntity = testPart.getPart();
                        if (partEntity != null) {
                                partName = partEntity.getName();
                        }
                }
                return new PartInfo(partName, partOrder);
        }

        private List<QuestionResultVM> buildOverallQuestionsList(Test test, QuestionResponseMaps responseMaps) {
                List<QuestionResultVM> directQuestions = buildDirectQuestions(test, responseMaps);
                List<QuestionResultVM> questionSetQuestions = buildQuestionSetQuestions(test, responseMaps);

                return Stream.concat(directQuestions.stream(), questionSetQuestions.stream())
                                .sorted(Comparator.comparing(QuestionResultVM::getOrder))
                                .collect(Collectors.toList());
        }

        private List<QuestionResultVM> buildDirectQuestions(Test test, QuestionResponseMaps responseMaps) {
                return test.getTestQuestionDetails().stream()
                                .sorted(Comparator.comparing(TestQuestionDetail::getOrder))
                                .map(detail -> mapTestQuestionDetailToVM(detail, responseMaps))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
        }

        private QuestionResultVM mapTestQuestionDetailToVM(TestQuestionDetail detail,
                        QuestionResponseMaps responseMaps) {
                Question question = detail.getQuestion();
                if (question == null) {
                        return null;
                }
                return buildQuestionResultVM(question, detail.getOrder(), responseMaps);
        }

        private List<QuestionResultVM> buildQuestionSetQuestions(Test test, QuestionResponseMaps responseMaps) {
                return test.getTestQuestionSetDetails().stream()
                                .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder))
                                .flatMap(detail -> mapQuestionSetDetailToVMs(detail, responseMaps))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
        }

        private Stream<QuestionResultVM> mapQuestionSetDetailToVMs(TestQuestionSetDetail detail,
                        QuestionResponseMaps responseMaps) {
                QuestionSet questionSet = detail.getQuestionSet();
                return questionSet.getQuestionSetItems().stream()
                                .sorted(Comparator.comparing(QuestionSetItem::getOrder))
                                .map(item -> {
                                        Question question = item.getQuestion();
                                        if (question == null) {
                                                return null;
                                        }
                                        int order = detail.getOrder() - 1 + item.getOrder();
                                        return buildQuestionResultVM(question, order, responseMaps);
                                });
        }

        private QuestionResultVM buildQuestionResultVM(Question question, int order,
                        QuestionResponseMaps responseMaps) {
                Set<UUID> selectedOptionIds = responseMaps.selectedOptionIds.getOrDefault(question.getId(),
                                Collections.emptySet());

                return QuestionResultVM.builder()
                                .order(order)
                                .context(question.getPrompt())
                                .explanation(null)
                                .transcript(question.getTranscript())
                                .questionType(question.getQuestionType() != null ? question.getQuestionType().getCode()
                                                : null)
                                .options(mapOptions(question, selectedOptionIds))
                                .correctOptions(mapCorrectOptions(question))
                                .correctAnswers(mapCorrectAnswers(question))
                                .userAnswer(responseMaps.textAnswers.get(question.getId()))
                                .isCorrect(responseMaps.correctness.get(question.getId()))
                                .questionAudios(mapQuestionAudios(question))
                                .build();
        }

        private List<OptionResultVM> mapOptions(Question question, Set<UUID> selectedOptionIds) {
                if (question.getOptions() == null) {
                        return Collections.emptyList();
                }
                return question.getOptions().stream()
                                .map(option -> OptionResultVM.builder()
                                                .id(option.getId().toString())
                                                .text(option.getText())
                                                .isCorrect(option.isCorrect())
                                                .isSelected(selectedOptionIds.contains(option.getId()))
                                                .build())
                                .collect(Collectors.toList());
        }

        private List<OptionResultVM> mapCorrectOptions(Question question) {
                if (question.getOptions() == null) {
                        return Collections.emptyList();
                }
                return question.getOptions().stream()
                                .filter(Option::isCorrect)
                                .map(option -> OptionResultVM.builder()
                                                .id(option.getId().toString())
                                                .text(option.getText())
                                                .isCorrect(true)
                                                .build())
                                .collect(Collectors.toList());
        }

        private List<String> mapCorrectAnswers(Question question) {
                if (question.getFillBlankAnswers() == null) {
                        return Collections.emptyList();
                }
                return question.getFillBlankAnswers().stream()
                                .map(FillBlankAnswer::getAnswerText)
                                .collect(Collectors.toList());
        }

        private List<FileInfoResultVM> mapQuestionAudios(Question question) {
                if (question.getFileInfos() == null || question.getFileInfos().isEmpty()) {
                        return null;
                }
                return question.getFileInfos().stream()
                                .map(this::mapFileInfoToVM)
                                .collect(Collectors.toList());
        }

        private Map<String, Map<Optional<UUID>, List<QuestionResultVM>>> groupQuestionsByCategoryAndPart(
                        List<QuestionResultDTO> questionDTOs) {
                Map<String, Map<Optional<UUID>, List<QuestionResultVM>>> categoryPartMap = new HashMap<>();

                for (QuestionResultDTO dto : questionDTOs) {
                        List<String> categories = getCategories(dto);
                        Optional<UUID> partId = parsePartId(dto);
                        QuestionResultVM vm = convertDTOToVM(dto);

                        for (String category : categories) {
                                categoryPartMap
                                                .computeIfAbsent(category, k -> new HashMap<>())
                                                .computeIfAbsent(partId, k -> new ArrayList<>())
                                                .add(vm);
                        }
                }
                return categoryPartMap;
        }

        private List<String> getCategories(QuestionResultDTO dto) {
                List<String> categories = dto.getQuestionCategories();
                if (categories == null || categories.isEmpty()) {
                        return Collections.singletonList("Uncategorized");
                }
                return categories;
        }

        private Optional<UUID> parsePartId(QuestionResultDTO dto) {
                if (dto.getPartId() == null || !dto.getPartId().isPresent()) {
                        return Optional.empty();
                }
                try {
                        return Optional.of(dto.getPartId().get());
                } catch (IllegalArgumentException e) {
                        log.warn("Invalid partId format: {}", dto.getPartId());
                        return Optional.empty();
                }
        }

        private QuestionResultVM convertDTOToVM(QuestionResultDTO dto) {
                return QuestionResultVM.builder()
                                .order(dto.getOrder())
                                .context(dto.getContext())
                                .explanation(dto.getExplanation())
                                .transcript(dto.getTranscript())
                                .questionType(dto.getQuestionType())
                                .isCorrect(dto.getIsCorrect())
                                .correctOptions(dto.getCorrectOptions())
                                .correctAnswers(dto.getCorrectAnswers())
                                .userAnswer(dto.getUserAnswer())
                                .options(dto.getOptions())
                                .questionCategories(dto.getQuestionCategories())
                                .build();
        }

        private List<AnalysisQuesCategory> buildAnalysisCategoriesFromMap(
                        Map<String, Map<Optional<UUID>, List<QuestionResultVM>>> categoryPartMap) {
                List<AnalysisQuesCategory> result = new ArrayList<>();

                for (Map.Entry<String, Map<Optional<UUID>, List<QuestionResultVM>>> categoryEntry : categoryPartMap
                                .entrySet()) {
                        String category = categoryEntry.getKey();
                        for (Map.Entry<Optional<UUID>, List<QuestionResultVM>> partEntry : categoryEntry.getValue()
                                        .entrySet()) {
                                AnalysisQuesCategory categoryAnalysis = buildAnalysisCategory(category,
                                                partEntry.getKey(), partEntry.getValue());
                                result.add(categoryAnalysis);
                        }
                }
                return result;
        }

        private AnalysisQuesCategory buildAnalysisCategory(String categoryName, Optional<UUID> partId,
                        List<QuestionResultVM> questions) {
                long correctCount = questions.stream()
                                .filter(q -> q.getIsCorrect() != null && q.getIsCorrect())
                                .count();

                long incorrectCount = questions.stream()
                                .filter(q -> q.getIsCorrect() != null && !q.getIsCorrect())
                                .count();

                long skipCount = questions.stream()
                                .filter(q -> q.getIsCorrect() == null)
                                .count();

                double accuracy = (correctCount + incorrectCount) == 0 ? 0.0
                                : (double) correctCount / (correctCount + incorrectCount) * 100;

                return AnalysisQuesCategory.builder()
                                .categoryName(categoryName)
                                .partId(partId)
                                .correctNumber((int) correctCount)
                                .incorrectNumber((int) incorrectCount)
                                .skipNumber((int) skipCount)
                                .accuracy(accuracy)
                                .questions(questions)
                                .build();
        }

        // ============= Helper Inner Classes =============

        private static class QuestionResponseMaps {
                final Map<UUID, Set<UUID>> selectedOptionIds;
                final Map<UUID, String> textAnswers;
                final Map<UUID, Boolean> correctness;

                QuestionResponseMaps(Map<UUID, Set<UUID>> selectedOptionIds, Map<UUID, String> textAnswers,
                                Map<UUID, Boolean> correctness) {
                        this.selectedOptionIds = selectedOptionIds;
                        this.textAnswers = textAnswers;
                        this.correctness = correctness;
                }
        }

        private static class PartInfo {
                final String name;
                final Integer order;

                PartInfo(String name, Integer order) {
                        this.name = name;
                        this.order = order;
                }
        }

        /**
         * Helper class to track a question along with its parent question set
         * description
         */
        private static class QuestionWithQuestionSetInfo {
                Question question;
                String questionSetDescription;

                QuestionWithQuestionSetInfo(Question question, String questionSetDescription) {
                        this.question = question;
                        this.questionSetDescription = questionSetDescription;
                }
        }
}
