package com.example.exam.prep.service;

import com.example.exam.prep.constant.status.TestStatus;
import com.example.exam.prep.exception.NotFoundException;
import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.model.Test;
import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.model.TestPart;
import com.example.exam.prep.model.TestPartAttempt;
import com.example.exam.prep.model.User;
import com.example.exam.prep.repository.ITestAttemptRepository;
import com.example.exam.prep.service.base.BaseService;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.example.exam.prep.vm.test.TestVM;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import com.example.exam.prep.model.viewmodels.TestAttemptInfoVM;
import com.example.exam.prep.model.viewmodels.TestAttemptWithNameVM;
import com.example.exam.prep.repository.IQuestionResponseRepository;
import com.example.exam.prep.repository.ITestQuestionDetailRepository;
import com.example.exam.prep.repository.ITestQuestionSetDetailRepository;

/**
 * Service implementation for managing test attempts.
 */
@Service
public class TestAttemptService extends BaseService<TestAttempt> implements ITestAttemptService {
    
    private final IUnitOfWork unitOfWork;
    private final IUserService userService;
    private final ITestService testService;
    private final IQuestionResponseRepository questionResponseRepository;
    private final ITestQuestionDetailRepository testQuestionDetailRepository;
    private final ITestQuestionSetDetailRepository testQuestionSetDetailRepository;
    
    public TestAttemptService(IUnitOfWork unitOfWork, 
                            IUserService userService, 
                            ITestService testService,
                            IQuestionResponseRepository questionResponseRepository,
                            ITestQuestionDetailRepository testQuestionDetailRepository,
                            ITestQuestionSetDetailRepository testQuestionSetDetailRepository) {
        super(unitOfWork.getTestAttemptRepository());
        this.unitOfWork = unitOfWork;
        this.userService = userService;
        this.testService = testService;
        this.questionResponseRepository = questionResponseRepository;
        this.testQuestionDetailRepository = testQuestionDetailRepository;
        this.testQuestionSetDetailRepository = testQuestionSetDetailRepository;
    }
    
    private ITestAttemptRepository getRepository() {
        return unitOfWork.getTestAttemptRepository();
    }

    @Override
    @Transactional
    public TestAttempt startTestAttempt(UUID testId, UUID userId) {
        User user = Optional.ofNullable(userService.getUser(userId))
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        // Get test VM and convert to entity
        TestVM testVM = testService.findById(testId);
        Test test = new Test();
        test.setId(testVM.getId());
        
        // Check if there's an existing ongoing attempt
        getRepository().findByTestIdAndUserId(testId, userId).stream()
                .filter(attempt -> attempt.getStatus() == TestStatus.ONGOING)
                .findFirst()
                .ifPresent(attempt -> {
                    throw new IllegalStateException("There is already an ongoing attempt for this test");
                });
        
        TestAttempt attempt = new TestAttempt();
        attempt.setUser(user);
        attempt.setTest(test);
        attempt.setStartTime(Instant.now());
        attempt.setStatus(TestStatus.ONGOING);
        
        return getRepository().save(attempt);
    }
    
    @Override
    @Transactional
    public TestAttempt submitTestAttempt(UUID attemptId, UUID userId) {
        TestAttempt attempt = getValidAttempt(attemptId, userId);
        
        if (attempt.getStatus() != TestStatus.ONGOING) {
            throw new IllegalStateException("Cannot submit a test attempt that is not in progress");
        }
        
        attempt.setStatus(TestStatus.SUBMITTED);
        attempt.setEndTime(Instant.now());
        
        return getRepository().save(attempt);
    }
    
    @Override
    @Transactional
    public TestAttempt updateTestAttemptStatus(UUID attemptId, TestStatus status) {
        TestAttempt attempt = getTestAttemptById(attemptId);
        
        attempt.setStatus(status);
        if (status == TestStatus.SUBMITTED && attempt.getEndTime() == null) {
            attempt.setEndTime(Instant.now());
        }
        
        return getRepository().save(attempt);
    }
    
    @Override
    public TestAttempt getTestAttemptById(UUID attemptId) {
        return getRepository().findById(attemptId)
                .orElseThrow(() -> new NotFoundException("Test attempt not found with id: " + attemptId));
    }
    
    @Override
    @Transactional
    public TestAttempt calculateAndUpdateScore(UUID attemptId) {
        TestAttempt attempt = getTestAttemptById(attemptId);
        
        if (attempt.getStatus() != TestStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot calculate score for an unsubmitted test attempt");
        }
        
        // TODO: Implement actual score calculation based on test part attempts
        // For now, we'll just set a placeholder score
        attempt.setTotalScore(0.0);
        
        return getRepository().save(attempt);
    }
    
    @Override
    public TestStatus getTestAttemptStatus(UUID attemptId) {
        return getTestAttemptById(attemptId).getStatus();
    }
    
    @Override
    public List<TestAttemptWithNameVM> getLatestTestAttemptsWithTestName(UUID userId, int limit, String timezone) {
        // Get the most recent test attempts for the user, ordered by creation date
        Page<TestAttempt> attemptPage = getRepository()
                .findByUserIdOrderByInsertedAtDesc(userId, PageRequest.of(0, limit));
        
        // Get timezone, default to system default if not provided
        ZoneId zoneId = (timezone != null && !timezone.isEmpty()) 
                ? parseTimeZone(timezone)
                : ZoneId.systemDefault();
        
        // Map each attempt to TestAttemptWithNameVM using the zoneId
        return attemptPage.getContent().stream()
                .map(attempt -> mapToTestAttemptInfoVM(attempt, zoneId))
                .collect(Collectors.toList());
    }
    
    private ZoneId parseTimeZone(String timezone) {
        try {
            // First decode the URL-encoded string to handle cases like "Asia%252FHo_Chi_Minh"
            String decoded = java.net.URLDecoder.decode(timezone, "UTF-8");
            return ZoneId.of(decoded);
        } catch (java.io.UnsupportedEncodingException e) {
            // Fallback to using the original string if decoding fails
            return ZoneId.of(timezone);
        } catch (Exception e) {
            // If there's any other error, use the system default
            return ZoneId.systemDefault();
        }
    }
    
    private TestAttempt getValidAttempt(UUID attemptId, UUID userId) {
        TestAttempt attempt = getTestAttemptById(attemptId);
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to access this test attempt");
        }
        
        return attempt;
    }

    private TestAttemptWithNameVM mapToTestAttemptInfoVM(TestAttempt attempt, ZoneId zone) {
        // First create the base VM with common fields
        TestAttemptInfoVM baseVm = new TestAttemptInfoVM();
        baseVm.setId(attempt.getId());
        Instant createdAt = attempt.getInsertedAt();
        baseVm.setTakeDate(createdAt);
        if (createdAt != null) {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(createdAt, zone);
            baseVm.setTakeDateLocal(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(zdt));
        }
        baseVm.setIsPractice(attempt.getIsPractice());
        baseVm.setStartTime(attempt.getStartTime());
        baseVm.setEndTime(attempt.getEndTime());
        baseVm.setDurationSeconds(attempt.getDurationSeconds());

        // Get test parts and sort by orderIndex from TestPart
        List<String> parts = attempt.getTestPartAttempts().stream()
                .map(partAttempt -> {
                    // Get the TestPart for this attempt's part
                    TestPart testPart = partAttempt.getPart().getTestParts().stream()
                            .filter(tp -> tp.getTest().equals(attempt.getTest()))
                            .findFirst()
                            .orElse(null);
                    return testPart != null ? 
                            Map.entry(testPart.getOrderIndex(), partAttempt.getPart().getName()) : 
                            null;
                })
                .filter(Objects::nonNull)
                .sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        baseVm.setParts(parts);

        // Calculate total questions based on practice mode
        int totalQuestions;
        if (attempt.getIsPractice() && !attempt.getTestPartAttempts().isEmpty()) {
            // For practice mode with part attempts, count questions in selected parts
            totalQuestions = calculateTotalQuestionsFromParts(attempt.getTest(), attempt.getTestPartAttempts());
        } else {
            // For non-practice mode or practice mode without part attempts, count all questions
            totalQuestions = calculateTotalQuestionsFromTest(attempt.getTest());
        }
        baseVm.setTotalQuestions(totalQuestions);
        
        // Count correct answers for this test attempt
        int correctAnswers = questionResponseRepository.countByTestAttemptAndIsCorrect(attempt, true);
        baseVm.setCorrectAnswers(correctAnswers);

        // Get the test name
        String testName = testService.findById(attempt.getTest().getId()).getTitle();
        
        // Create and return the TestAttemptWithNameVM
        TestAttemptWithNameVM result = new TestAttemptWithNameVM();
        result.setId(attempt.getId());
        result.setTakeDate(baseVm.getTakeDate());
        result.setTakeDateLocal(baseVm.getTakeDateLocal());
        result.setIsPractice(baseVm.getIsPractice());
        result.setStartTime(baseVm.getStartTime());
        result.setEndTime(baseVm.getEndTime());
        result.setDurationSeconds(baseVm.getDurationSeconds());
        result.setParts(baseVm.getParts());
        result.setTotalQuestions(baseVm.getTotalQuestions());
        result.setCorrectAnswers(baseVm.getCorrectAnswers());
        result.setTestName(testName);
        return result;
    }

    private int calculateTotalQuestionsFromParts(Test test, Set<TestPartAttempt> partAttempts) {
        return partAttempts.stream()
                .map(partAttempt -> test.getTestParts().stream()
                        .filter(testPart -> testPart.getPart().getId().equals(partAttempt.getPart().getId()))
                        .mapToInt(testPart -> testPart.getTestPartQuestions().size() +
                                testPart.getTestPartQuestionSets().stream()
                                        .mapToInt(questionSet -> questionSet.getQuestionSet().getQuestions().size())
                                        .sum())
                        .sum())
                .mapToInt(Integer::intValue)
                .sum();
    }

    private int calculateTotalQuestionsFromTest(Test test) {
        // If test has parts, calculate from parts
        if (test.getTestParts() != null && !test.getTestParts().isEmpty()) {
            return test.getTestParts().stream()
                    .mapToInt(testPart -> testPart.getTestPartQuestions().size() +
                            testPart.getTestPartQuestionSets().stream()
                                    .mapToInt(questionSet -> questionSet.getQuestionSet().getQuestions().size())
                                    .sum())
                    .sum();
        }
        
        // If no parts, count directly from TestQuestionDetail and TestQuestionSetDetail
        int directQuestions = testQuestionDetailRepository.countByTestId(test.getId());
        
        int questionsFromSets = testQuestionSetDetailRepository.findByTestId(test.getId()).stream()
                .mapToInt(detail -> {
                    QuestionSet questionSet = detail.getQuestionSet();
                    return questionSet != null ? questionSet.getQuestionSetItems().size() : 0;
                })
                .sum();
                
        return directQuestions + questionsFromSets;
    }
}

