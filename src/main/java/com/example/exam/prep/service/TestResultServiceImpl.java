package com.example.exam.prep.service;

import com.example.exam.prep.model.*;
import com.example.exam.prep.model.Part;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.example.exam.prep.vm.testresult.AnswerResultVM;
import com.example.exam.prep.vm.testresult.QuestionResultVM;
import com.example.exam.prep.vm.testresult.OptionResultVM;
import com.example.exam.prep.vm.testresult.PartResultVM;
import com.example.exam.prep.vm.testresult.TestResultOverallVM;
import java.util.stream.Stream;
import java.util.Comparator;
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

    @Override
    public TestResultOverallVM getTestResultOverall(UUID testId, UUID attemptId) {
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
                        .filter(tp -> tp.getTest().getId().equals(testId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Test part not found for test ID: " + testId));

                // Count direct questions in the test part
                int directQuestions = testPart.getTestPartQuestions() != null ? testPart.getTestPartQuestions().size()
                        : 0;

                // Count questions in question sets
                int questionsInSets = 0;
                if (testPart.getTestPartQuestionSets() != null) {
                    for (TestPartQuestionSet questionSet : testPart.getTestPartQuestionSets()) {
                        if (questionSet.getQuestionSet() != null
                                && questionSet.getQuestionSet().getQuestionSetItems() != null) {
                            questionsInSets += questionSet.getQuestionSet().getQuestionSetItems().size();
                        }
                    }
                }

                totalQuestions += directQuestions + questionsInSets;
            }
        } else {
            // If no test part attempts, count questions directly from TestQuestionDetail
            // and TestQuestionSetDetail
            List<TestQuestionDetail> testQuestionDetails = unitOfWork.getTestQuestionDetailRepository()
                    .findByTestId(testId);

            List<TestQuestionSetDetail> testQuestionSetDetails = unitOfWork.getTestQuestionSetDetailRepository()
                    .findByTestId(testId);

            // Count direct questions
            int directQuestions = testQuestionDetails != null ? testQuestionDetails.size() : 0;

            // Count questions in question sets
            int questionsInSets = 0;
            if (testQuestionSetDetails != null) {
                for (TestQuestionSetDetail setDetail : testQuestionSetDetails) {
                    if (setDetail.getQuestionSet() != null &&
                            setDetail.getQuestionSet().getQuestionSetItems() != null) {
                        questionsInSets += setDetail.getQuestionSet().getQuestionSetItems().size();
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
                .accuracyPercentage(((double) correctQuestionsCount / totalAnsweredQuestion) * 100)
                .score(totalScore)
                .completionTime(TimeFormatHelper.formatSecondsToHms(testAttempt.getDurationSeconds()))
                .build();
    }

    @Override
    public AnswerResultVM getTestAnswers(UUID attemptId) {
        TestAttempt testAttempt = unitOfWork.getTestAttemptRepository().findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Test attempt not found for ID: " + attemptId));

        // Get all question responses for this attempt
        List<QuestionResponse> questionResponses = unitOfWork.getQuestionResponseRepository()
                .findByTestAttemptId(attemptId);
                
        // Create maps for quick lookup
        Map<UUID, Set<UUID>> questionToSelectedOptionIds = new HashMap<>();
        Map<UUID, String> questionToTextAnswer = new HashMap<>();
        Map<UUID, Boolean> questionToIsCorrect = new HashMap<>();
        
        for (QuestionResponse response : questionResponses) {
            UUID questionId = response.getQuestion().getId();
            
            // Map selected option IDs
            questionToSelectedOptionIds.put(
                questionId,
                response.getSelectedOptions().stream()
                    .map(opt -> opt.getOption().getId())
                    .collect(Collectors.toSet())
            );
            
            // Map text answers
            if (response.getTextAnswer() != null && !response.getTextAnswer().isEmpty()) {
                questionToTextAnswer.put(questionId, response.getTextAnswer());
            }
            
            // Map correctness
            if (response.getIsCorrect() != null) {
                questionToIsCorrect.put(questionId, response.getIsCorrect());
            }
        }

        List<TestPartAttempt> testPartAttempts = unitOfWork.getTestPartAttemptRepository()
                .findByTestAttemptId(attemptId);

        List<Part> parts = testPartAttempts.stream()
                .map(TestPartAttempt::getPart)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<PartResultVM> partResults = parts.stream()
                .map(part -> {
                    UUID partId = part.getId();
                    UUID testId = testAttempt.getTest().getId();
                    
                    // Get all questions for this part
                    List<QuestionResultVM> questionResults = unitOfWork.getTestPartRepository()
                            .findByPartIdAndTestId(partId, testId)
                            .stream()
                            .flatMap(testPart -> {
                                // Get questions from testPartQuestions
                                Stream<Question> directQuestions = testPart.getTestPartQuestions().stream()
                                        .map(TestPartQuestion::getQuestion);
                                
                                // Get questions from testPartQuestionSets
                                Stream<Question> questionSetQuestions = testPart.getTestPartQuestionSets().stream()
                                        .flatMap(qs -> qs.getQuestionSet().getQuestionSetItems().stream()
                                                .map(QuestionSetItem::getQuestion));
                                
                                // Combine both streams and map to QuestionResultVM
                                return Stream.concat(directQuestions, questionSetQuestions)
                                        .map(question -> {
                                            QuestionResultVM.QuestionResultVMBuilder builder = QuestionResultVM.builder()
                                                    .order(question.getOrder() != null ? question.getOrder() : 0)
                                                    .context(question.getPrompt());
                                            
                                            // Set options if available
                                            if (question.getOptions() != null) {
                                                builder.options(question.getOptions().stream()
                                                        .map(option -> OptionResultVM.builder()
                                                                .id(option.getId().toString())
                                                                .text(option.getText())
                                                                .isCorrect(option.isCorrect())
                                                                .build())
                                                        .collect(Collectors.toList()));
                                                
                                                // Keep the correct options for backward compatibility
                                                builder.correctOptions(question.getOptions().stream()
                                                        .filter(Option::isCorrect)
                                                        .map(option -> OptionResultVM.builder()
                                                                .id(option.getId().toString())
                                                                .text(option.getText())
                                                                .isCorrect(true)
                                                                .build())
                                                        .collect(Collectors.toList()));
                                            }
                                            
                                            // Set fill-in-blank answers if available
                                            if (question.getFillBlankAnswers() != null) {
                                                builder.correctAnswers(question.getFillBlankAnswers().stream()
                                                        .map(FillBlankAnswer::getAnswerText)
                                                        .collect(Collectors.toList()));
                                            }
                                            builder.userAnswer(questionToTextAnswer.get(question.getId()));
                                            builder.isCorrect(questionToIsCorrect.get(question.getId()));
                                            return builder.build();
                                        });
                            })
                            .collect(Collectors.toList());
                    
                    // Get part name and order
                    String partName = "Part " + partId.toString();
                    Integer partOrder = null;
                    
                    // Find the test part to get the order index
                    Optional<TestPart> testPartOpt = unitOfWork.getTestPartRepository()
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
        List<QuestionResultVM> directQuestions = test.getTestQuestionDetails().stream()
                .sorted(Comparator.comparing(TestQuestionDetail::getOrder))
                .map(detail -> {
                    Question question = detail.getQuestion();
                    return QuestionResultVM.builder()
                            .order(detail.getOrder())
                            .context(question.getPrompt())  // Using prompt as context
                            .explanation(null)  // No explanation field in Question model
                            .transcript(question.getAudioUrl())  // Using audioUrl as transcript
                            .options(question.getOptions() != null ? 
                                    question.getOptions().stream()
                                            .map(option -> {
                                                Set<UUID> selectedOptionIds = questionToSelectedOptionIds.getOrDefault(question.getId(), Collections.emptySet());
                                                return OptionResultVM.builder()
                                                        .id(option.getId().toString())
                                                        .text(option.getText())
                                                        .isCorrect(option.isCorrect())
                                                        .isSelected(selectedOptionIds.contains(option.getId()))
                                                        .build();
                                            })
                                            .collect(Collectors.toList()) : 
                                    Collections.emptyList())
                            .correctOptions(question.getOptions() != null ? 
                                    question.getOptions().stream()
                                            .filter(Option::isCorrect)
                                            .map(option -> OptionResultVM.builder()
                                                    .id(option.getId().toString())
                                                    .text(option.getText())
                                                    .isCorrect(true)
                                                    .build())
                                            .collect(Collectors.toList()) : 
                                    Collections.emptyList())
                            .correctAnswers(question.getFillBlankAnswers() != null ? 
                                    question.getFillBlankAnswers().stream()
                                            .map(FillBlankAnswer::getAnswerText)
                                            .collect(Collectors.toList()) : 
                                    Collections.emptyList())
                            .userAnswer(questionToTextAnswer.get(question.getId()))
                            .isCorrect(questionToIsCorrect.get(question.getId()))
                            .build();
                })
                .collect(Collectors.toList());
        
        // Get questions from question sets in testQuestionSetDetails
        List<QuestionResultVM> questionSetQuestions = test.getTestQuestionSetDetails().stream()
                .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder))
                .flatMap(detail -> {
                    QuestionSet questionSet = detail.getQuestionSet();
                    return questionSet.getQuestionSetItems().stream()
                            .sorted(Comparator.comparing(QuestionSetItem::getOrder))
                            .map(item -> {
                                Question question = item.getQuestion();
                                return QuestionResultVM.builder()
                                        .order(detail.getOrder() * 1000 + item.getOrder()) // Offset by 1000 to keep ordering between sets
                                        .context(question.getPrompt())  // Using prompt as context
                                        .explanation(null)  // No explanation field in Question model
                                        .transcript(question.getAudioUrl())  // Using audioUrl as transcript
                                        .options(question.getOptions() != null ?
                                                question.getOptions().stream()
                                                        .map(option -> OptionResultVM.builder()
                                                                .id(option.getId().toString())
                                                                .text(option.getText())
                                                                .isCorrect(option.isCorrect())
                                                                .build())
                                                        .collect(Collectors.toList()) :
                                                Collections.emptyList())
                                        .correctOptions(question.getOptions() != null ? 
                                                question.getOptions().stream()
                                                        .filter(Option::isCorrect)
                                                        .map(option -> OptionResultVM.builder()
                                                                .id(option.getId().toString())
                                                                .text(option.getText())
                                                                .isCorrect(true)
                                                                .build())
                                                        .collect(Collectors.toList()) : 
                                                Collections.emptyList())
                                        .correctAnswers(question.getFillBlankAnswers() != null ? 
                                                question.getFillBlankAnswers().stream()
                                                        .map(FillBlankAnswer::getAnswerText)
                                                        .collect(Collectors.toList()) : 
                                                Collections.emptyList())
                                        .userAnswer(questionToTextAnswer.get(question.getId()))
                            .isCorrect(questionToIsCorrect.get(question.getId()))
                                        .build();
                            });
                })
                .collect(Collectors.toList());
        
        // Combine both lists and sort by order
        List<QuestionResultVM> allQuestions = Stream.concat(directQuestions.stream(), questionSetQuestions.stream())
                .sorted(Comparator.comparing(QuestionResultVM::getOrder))
                .collect(Collectors.toList());
        
        // Sort parts by order
        List<PartResultVM> sortedParts = partResults.stream()
                .sorted(Comparator.comparing(PartResultVM::getOrder, 
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
                
        // Build the final result
        return AnswerResultVM.builder()
                .parts(sortedParts)
                .overall(allQuestions)
                .build();
    }
}
