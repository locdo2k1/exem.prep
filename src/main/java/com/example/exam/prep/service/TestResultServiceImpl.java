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

                int totalQuestions = 0;
                if (!testPartAttempts.isEmpty()) {
                        for (TestPartAttempt testPartAttempt : testPartAttempts) {
                                Part part = testPartAttempt.getPart();
                                TestPart testPart = part.getTestParts().stream()
                                                .filter(tp -> tp.getTest().getId()
                                                                .equals(testAttempt.getTest().getId()))
                                                .findFirst()
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Test part not found for test ID: "
                                                                                + testAttempt.getTest().getId()));

                                // Count direct questions in the test part
                                int directQuestions = testPart.getTestPartQuestions() != null
                                                ? testPart.getTestPartQuestions().size()
                                                : 0;

                                // Count questions in question sets
                                int questionsInSets = 0;
                                if (testPart.getTestPartQuestionSets() != null) {
                                        for (TestPartQuestionSet questionSet : testPart.getTestPartQuestionSets()) {
                                                if (questionSet.getQuestionSet() != null
                                                                && questionSet.getQuestionSet()
                                                                                .getQuestionSetItems() != null) {
                                                        questionsInSets += questionSet.getQuestionSet()
                                                                        .getQuestionSetItems().size();
                                                }
                                        }
                                }

                                totalQuestions += directQuestions + questionsInSets;
                        }
                } else {
                        // If no test part attempts, count questions directly from TestQuestionDetail
                        // and TestQuestionSetDetail
                        List<TestQuestionDetail> testQuestionDetails = unitOfWork.getTestQuestionDetailRepository()
                                        .findByTestId(testAttempt.getTest().getId());

                        List<TestQuestionSetDetail> testQuestionSetDetails = unitOfWork
                                        .getTestQuestionSetDetailRepository()
                                        .findByTestId(testAttempt.getTest().getId());

                        // Count direct questions
                        int directQuestions = testQuestionDetails != null ? testQuestionDetails.size() : 0;

                        // Count questions in question sets
                        int questionsInSets = 0;
                        if (testQuestionSetDetails != null) {
                                for (TestQuestionSetDetail setDetail : testQuestionSetDetails) {
                                        if (setDetail.getQuestionSet() != null &&
                                                        setDetail.getQuestionSet().getQuestionSetItems() != null) {
                                                questionsInSets += setDetail.getQuestionSet().getQuestionSetItems()
                                                                .size();
                                        }
                                }
                        }

                        totalQuestions = directQuestions + questionsInSets;
                }

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
    List<UUID> attemptedPartIds = unitOfWork.getTestPartAttemptRepository()
            .findByTestAttemptId(attemptId)
            .stream()
            .map(testPartAttempt -> testPartAttempt.getPart().getId())
            .collect(Collectors.toList());

    // Convert all questions to analysis categories
    List<AnalysisQuesCategory> allAnalysisCategories = convertToAnalysisByCategory(allQuestions);

    // Filter categories to only include attempted parts or categories without a part
    List<AnalysisQuesCategory> filteredCategories = allAnalysisCategories.stream()
            .filter(category -> 
                !category.getPartId().isPresent() ||  // Keep categories without a part
                (category.getPartId().isPresent() && 
                 attemptedPartIds.contains(category.getPartId().get()))  // Only keep categories for attempted parts
            )
            .collect(Collectors.toList());

    // Separate general questions (without partId) from part-specific questions
    Map<Boolean, List<AnalysisQuesCategory>> partitionedCategories = filteredCategories.stream()
            .collect(Collectors.partitioningBy(cat -> cat.getPartId().isPresent()));

    // Rest of the method remains the same...
    // Get general questions for overall analysis
    List<AnalysisQuesCategory> overallCategories = partitionedCategories.get(false);

    // Process part-specific questions
    Map<Optional<UUID>, List<AnalysisQuesCategory>> categoriesByPart = partitionedCategories.get(true)
            .stream()
            .collect(Collectors.groupingBy(AnalysisQuesCategory::getPartId));

    // Create part analysis for each part
    List<AnalysisPartVM> parts = categoriesByPart.entrySet().stream()
            .map(entry -> {
                UUID partId = entry.getKey().orElse(null);
                List<AnalysisQuesCategory> categories = entry.getValue();

                AnalysisPartVM partVM = new AnalysisPartVM();
                // Find part name if partId exists
                Part part = unitOfWork.getPartRepository().findById(partId)
                        .orElse(null);
                TestPart testPart = unitOfWork.getTestPartRepository()
                        .findByPartIdAndTestId(partId,
                                testAttempt.getTest().getId())
                        .orElse(null);
                if (part != null) {
                    partVM.setPartName(part.getName());
                }

                if (testPart != null) {
                    partVM.setOrder(testPart.getOrderIndex());
                }
                partVM.setCategories(categories);
                return partVM;
            })
            .collect(Collectors.toList());

    return AnalysisQuestionsVM.builder()
            .parts(parts.stream()
                    .sorted(Comparator.comparing(AnalysisPartVM::getOrder))
                    .collect(Collectors.toList()))
            .overall(overallCategories)
            .build();
}

        @Override
        public AnswerResultVM getTestAnswers(UUID attemptId) {
                TestAttempt testAttempt =
                unitOfWork.getTestAttemptRepository().findById(attemptId).orElseThrow(
                        ()
                        -> new RuntimeException(
                                "Test attempt not found for ID: " + attemptId));

                // Get all question responses for this attempt
                List<QuestionResponse> questionResponses =
                unitOfWork.getQuestionResponseRepository().findByTestAttemptId(attemptId);

                // Create maps for quick lookup
                Map<UUID, Set<UUID>> questionToSelectedOptionIds = new HashMap<>();
                Map<UUID, String> questionToTextAnswer = new HashMap<>();
                Map<UUID, Boolean> questionToIsCorrect = new HashMap<>();

                for (QuestionResponse response : questionResponses) {
                UUID questionId = response.getQuestion().getId();

                // Map selected option IDs
                questionToSelectedOptionIds.put(questionId,
                        response.getSelectedOptions()
                        .stream()
                        .map(opt -> opt.getOption().getId())
                        .collect(Collectors.toSet()));

                // Map text answers
                if (response.getTextAnswer() != null
                        && !response.getTextAnswer().isEmpty()) {
                questionToTextAnswer.put(questionId, response.getTextAnswer());
                }

                // Map correctness
                if (response.getIsCorrect() != null) {
                questionToIsCorrect.put(questionId, response.getIsCorrect());
                }
                }

                List<TestPartAttempt> testPartAttempts =
                unitOfWork.getTestPartAttemptRepository().findByTestAttemptId(attemptId);

                List<Part> parts = testPartAttempts.stream()
                                        .map(TestPartAttempt::getPart)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());

                List<PartResultVM> partResults =
                parts.stream()
                        .map(part -> {
                        UUID partId = part.getId();
                        UUID testId = testAttempt.getTest().getId();

                        // Get all questions for this part
                        List<QuestionResultVM> questionResults =
                                unitOfWork.getTestPartRepository()
                                .findByPartIdAndTestId(partId, testId)
                                .stream()
                                .flatMap(testPart -> {
                                // Get questions from testPartQuestions with null checks
                                Stream<Question> directQuestions =
                                testPart.getTestPartQuestions().stream()
                                        .filter(Objects::nonNull)
                                        .map(tpq -> {
                                            if (tpq == null || tpq.getQuestion() == null) {
                                                return null;
                                            }
                                            Question original = tpq.getQuestion();
                                            if (tpq.getDisplayOrder() != null) {
                                                original.setOrder(tpq.getDisplayOrder());
                                            }
                                            return original;
                                        })
                                        .filter(Objects::nonNull);

                                // Get questions from testPartQuestionSets with null checks
                                Stream<Question> questionSetQuestions =
                                testPart.getTestPartQuestionSets().stream()
                                        .filter(Objects::nonNull)
                                        .flatMap(qs -> {
                                            if (qs == null || qs.getQuestionSet() == null || qs.getQuestionSet().getQuestionSetItems() == null) {
                                                return Stream.empty();
                                            }
                                            return qs.getQuestionSet().getQuestionSetItems().stream()
                                                    .filter(Objects::nonNull)
                                                    .sorted(Comparator.comparing(
                                                            QuestionSetItem::getOrder,
                                                            Comparator.nullsLast(Comparator.naturalOrder())
                                                    ))
                                                    .map(qsi -> {
                                                        if (qsi == null || qsi.getQuestion() == null) {
                                                            return null;
                                                        }
                                                        Question original = qsi.getQuestion();
                                                        Integer order = qsi.getOrder();
                                                        Integer displayOrder = qs.getDisplayOrder();
                                                        if (order != null && displayOrder != null) {
                                                            original.setOrder(order - 1 + displayOrder);
                                                        }
                                                        return original;
                                                    })
                                                    .filter(Objects::nonNull);
                                        });


                                // Combine both streams and map to QuestionResultVM with null checks
                                return Stream
                                        .concat(directQuestions, questionSetQuestions)
                                        .filter(Objects::nonNull)
                                        .map(question -> {
                                            // Skip if question is null
                                            if (question == null) {
                                                return null;
                                            }

                                            QuestionResultVM.QuestionResultVMBuilder builder =
                                                    QuestionResultVM.builder()
                                                            .order(question.getOrder() != null ? question.getOrder() : 0)
                                                            .context(question.getPrompt() != null ? question.getPrompt() : "");

                                            // Set options if available
                                            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                                                // Map options with null checks
                                                List<OptionResultVM> options = question.getOptions().stream()
                                                        .filter(Objects::nonNull)
                                                        .map(option -> {
                                                            if (option == null) {
                                                                return null;
                                                            }
                                                            Set<UUID> selectedOptionIds = questionToSelectedOptionIds.getOrDefault(
                                                                    question.getId(),
                                                                    Collections.emptySet()
                                                            );
                                                            return OptionResultVM.builder()
                                                                    .id(option.getId() != null ? option.getId().toString() : "")
                                                                    .text(option.getText() != null ? option.getText() : "")
                                                                    .isCorrect(option.isCorrect())
                                                                    .isSelected(option.getId() != null && selectedOptionIds.contains(option.getId()))
                                                                    .build();
                                                        })
                                                        .filter(Objects::nonNull)
                                                        .collect(Collectors.toList());

                                                builder.options(options);

                                                // Keep the correct options for backward compatibility
                                                List<OptionResultVM> correctOptions = question.getOptions().stream()
                                                        .filter(Objects::nonNull)
                                                        .filter(Option::isCorrect)
                                                        .map(option -> OptionResultVM.builder()
                                                                .id(option.getId() != null ? option.getId().toString() : "")
                                                                .text(option.getText() != null ? option.getText() : "")
                                                                .isCorrect(true)
                                                                .build())
                                                        .collect(Collectors.toList());

                                                builder.correctOptions(correctOptions);
                                        }

                                        // Set fill-in-blank answers if available
                                        if (question.getFillBlankAnswers() != null && !question.getFillBlankAnswers().isEmpty()) {
                                            builder.correctAnswers(
                                                question.getFillBlankAnswers().stream()
                                                    .filter(Objects::nonNull)
                                                    .map(FillBlankAnswer::getAnswerText)
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList())
                                            );
                                        }

                                        // Set user answer if available
                                        if (question.getId() != null) {
                                            builder.userAnswer(questionToTextAnswer.get(question.getId()));
                                            Boolean isCorrect = questionToIsCorrect.get(question.getId());
                                            if (isCorrect != null) {
                                                builder.isCorrect(isCorrect);
                                            }
                                        }

                                        return builder.build();
                                        });
                                })
                                .collect(Collectors.toList());

                        // Get part name and order
                        String partName = "Part " + partId.toString();
                        Integer partOrder = null;

                        // Find the test part to get the order index
                        Optional<TestPart> testPartOpt =
                                unitOfWork.getTestPartRepository()
                                .findByPartIdAndTestId(partId, testId)
                                .stream()
                                .findFirst();

                        if (testPartOpt.isPresent()) {
                        TestPart testPart = testPartOpt.get();
                        partOrder = testPart.getOrderIndex();

                        // Get part name from the part entity
                        Part partEntity = testPart.getPart();
                        if (partEntity != null) {
                                partName = partEntity.getName();
                        }
                        }

                        return PartResultVM.builder()
                                .partId(partId.toString())
                                .name(partName)
                                .order(partOrder)
                                .questions(questionResults)
                                .build();
                        })
                        .collect(Collectors.toList());

                // Get all questions across all parts for the overall list
                Test test = testAttempt.getTest();

                // Get direct questions from testQuestionDetails
                List<QuestionResultVM> directQuestions =
                test.getTestQuestionDetails()
                        .stream()
                        .sorted(Comparator.comparing(TestQuestionDetail::getOrder))
                        .map(detail -> {
                        Question question = detail.getQuestion();
                        return QuestionResultVM.builder()
                                .order(detail.getOrder())
                                .context(question.getPrompt()) // Using prompt as context
                                .explanation(null) // No explanation field in Question model
                                .transcript(question.getAudioUrl()) // Using audioUrl as
                                // transcript
                                .options(question.getOptions() != null
                                        ? question.getOptions()
                                        .stream()
                                        .map(option -> {
                                                Set<UUID> selectedOptionIds =
                                                questionToSelectedOptionIds.getOrDefault(
                                                        question.getId(),
                                                        Collections.emptySet());
                                                return OptionResultVM.builder()
                                                .id(option.getId().toString())
                                                .text(option.getText())
                                                .isCorrect(option.isCorrect())
                                                .isSelected(selectedOptionIds.contains(
                                                        option.getId()))
                                                .build();
                                        })
                                        .collect(Collectors.toList())
                                        : Collections.emptyList())
                                .correctOptions(question.getOptions() != null
                                        ? question.getOptions()
                                        .stream()
                                        .filter(Option::isCorrect)
                                        .map(option
                                                -> OptionResultVM.builder()
                                                        .id(option.getId().toString())
                                                        .text(option.getText())
                                                        .isCorrect(true)
                                                        .build())
                                        .collect(Collectors.toList())
                                        : Collections.emptyList())
                                .correctAnswers(question.getFillBlankAnswers() != null
                                        ? question.getFillBlankAnswers()
                                        .stream()
                                        .map(FillBlankAnswer::getAnswerText)
                                        .collect(Collectors.toList())
                                        : Collections.emptyList())
                                .userAnswer(questionToTextAnswer.get(question.getId()))
                                .isCorrect(questionToIsCorrect.get(question.getId()))
                                .build();
                        })
                        .collect(Collectors.toList());

                // Get questions from question sets in testQuestionSetDetails
                List<QuestionResultVM> questionSetQuestions =
                test.getTestQuestionSetDetails()
                        .stream()
                        .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder))
                        .flatMap(detail -> {
                        QuestionSet questionSet = detail.getQuestionSet();
                        return questionSet.getQuestionSetItems()
                                .stream()
                                .sorted(Comparator.comparing(QuestionSetItem::getOrder))
                                .map(item -> {
                                Question question = item.getQuestion();
                                return QuestionResultVM.builder()
                                .order(detail.getOrder() - 1 + item.getOrder())
                                .context(question.getPrompt())
                                .explanation(null)
                                .transcript(question.getAudioUrl())
                                .options(question.getOptions() != null
                                        ? question.getOptions()
                                                .stream()
                                                .map(option
                                                        -> OptionResultVM.builder()
                                                        .id(option.getId().toString())
                                                        .text(option.getText())
                                                        .isCorrect(option.isCorrect())
                                                        .build())
                                                .collect(Collectors.toList())
                                        : Collections.emptyList())
                                .correctOptions(question.getOptions() != null
                                        ? question.getOptions()
                                                .stream()
                                                .filter(Option::isCorrect)
                                                .map(option
                                                        -> OptionResultVM.builder()
                                                        .id(option.getId().toString())
                                                        .text(option.getText())
                                                        .isCorrect(true)
                                                        .build())
                                                .collect(Collectors.toList())
                                        : Collections.emptyList())
                                .correctAnswers(question.getFillBlankAnswers() != null
                                        ? question.getFillBlankAnswers()
                                                .stream()
                                                .map(FillBlankAnswer::getAnswerText)
                                                .collect(Collectors.toList())
                                        : Collections.emptyList())
                                .userAnswer(questionToTextAnswer.get(question.getId()))
                                .isCorrect(questionToIsCorrect.get(question.getId()))
                                .build();
                                });
                        })
                        .collect(Collectors.toList());

                // Combine both lists and sort by order
                List<QuestionResultVM> allQuestions =
                Stream.concat(directQuestions.stream(), questionSetQuestions.stream())
                        .sorted(Comparator.comparing(QuestionResultVM::getOrder))
                        .collect(Collectors.toList());

                // Sort parts by order and questions within each part
                List<PartResultVM> sortedParts =
                partResults.stream()
                        .peek(part -> {
                            if (part.getQuestions() != null) {
                                part.getQuestions().sort(Comparator.comparing(QuestionResultVM::getOrder));
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
                Map<String, Map<Optional<UUID>, List<QuestionResultVM>>> categoryPartMap = new HashMap<>();

                // First pass: Group questions by their categories and partId
                for (QuestionResultDTO dto : questionDTOs) {
                        List<String> categories = dto.getQuestionCategories();
                        if (categories == null || categories.isEmpty()) {
                                // If no categories, put in "Uncategorized" category
                                categories = Collections.singletonList("Uncategorized");
                        }

                        // Parse partId from string to Optional<UUID>
                        Optional<UUID> partId = Optional.empty();
                        if (dto.getPartId() != null && !dto.getPartId().isEmpty()) {
                                try {
                                        partId = Optional.of(dto.getPartId().get());
                                } catch (IllegalArgumentException e) {
                                        log.warn("Invalid partId format: {}", dto.getPartId());
                                }
                        }

                        // Convert DTO to VM
                        QuestionResultVM vm = QuestionResultVM.builder()
                                        .order(dto.getOrder())
                                        .context(dto.getContext())
                                        .explanation(dto.getExplanation())
                                        .transcript(dto.getTranscript())
                                        .isCorrect(dto.getIsCorrect())
                                        .correctOptions(dto.getCorrectOptions())
                                        .correctAnswers(dto.getCorrectAnswers())
                                        .userAnswer(dto.getUserAnswer())
                                        .options(dto.getOptions())
                                        .questionCategories(dto.getQuestionCategories())
                                        .build();

                        // Add to each category and partId combination
                        for (String category : categories) {
                                categoryPartMap
                                                .computeIfAbsent(category, k -> new HashMap<>())
                                                .computeIfAbsent(partId, k -> new ArrayList<>())
                                                .add(vm);
                        }
                }

                // Second pass: Create AnalysisQuesCategory for each category and partId
                // combination
                List<AnalysisQuesCategory> result = new ArrayList<>();

                for (Map.Entry<String, Map<Optional<UUID>, List<QuestionResultVM>>> categoryEntry : categoryPartMap
                                .entrySet()) {
                        String category = categoryEntry.getKey();

                        for (Map.Entry<Optional<UUID>, List<QuestionResultVM>> partEntry : categoryEntry.getValue()
                                        .entrySet()) {
                                Optional<UUID> partId = partEntry.getKey();
                                List<QuestionResultVM> questions = partEntry.getValue();

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

                                AnalysisQuesCategory categoryAnalysis = AnalysisQuesCategory.builder()
                                                .categoryName(category)
                                                .partId(partId)
                                                .correctNumber((int) correctCount)
                                                .incorrectNumber((int) incorrectCount)
                                                .skipNumber((int) skipCount)
                                                .accuracy(accuracy)
                                                .questions(questions)
                                                .build();

                                result.add(categoryAnalysis);
                        }
                }

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
                                .testName(test.getName())
                                .partNames(partNames)
                                .build();
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
                                .transcript(question.getAudioUrl()) // Using audioUrl as transcript
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
}
