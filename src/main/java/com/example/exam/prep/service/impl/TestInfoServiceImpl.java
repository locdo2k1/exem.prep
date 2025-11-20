package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.*;
import com.example.exam.prep.repository.IQuestionResponseRepository;
import com.example.exam.prep.repository.ITestQuestionSetDetailRepository;
import com.example.exam.prep.model.TestPartAttempt;
import com.example.exam.prep.model.TestPartQuestion;
import com.example.exam.prep.model.TestPartQuestionSet;
import com.example.exam.prep.model.User;
import com.example.exam.prep.model.viewmodels.PracticeTestInfoVM;
import com.example.exam.prep.model.viewmodels.TestAttemptInfoVM;
import com.example.exam.prep.model.viewmodels.TestListItemVM;
import com.example.exam.prep.repository.ITestAttemptRepository;
import com.example.exam.prep.repository.ITestQuestionDetailRepository;
import com.example.exam.prep.repository.ITestRepository;
import com.example.exam.prep.repository.IUserRepository;
import com.example.exam.prep.repository.ITestPartRepository;
import com.example.exam.prep.service.ITestInfoService;
import com.example.exam.prep.viewmodel.TestPartInfoVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@Transactional
public class TestInfoServiceImpl implements ITestInfoService {
    private final ITestRepository testRepository;
    private final ITestPartRepository testPartRepository;
    private final ITestQuestionDetailRepository testQuestionDetailRepository;
    private final ITestQuestionSetDetailRepository testQuestionSetDetailRepository;
    private final ITestAttemptRepository testAttemptRepository;
    private final IUserRepository userRepository;
    private final IQuestionResponseRepository questionResponseRepository;

    public TestInfoServiceImpl(
            ITestRepository testRepository,
            ITestPartRepository testPartRepository,
            ITestQuestionDetailRepository testQuestionDetailRepository,
            ITestQuestionSetDetailRepository testQuestionSetDetailRepository,
            ITestAttemptRepository testAttemptRepository,
            IUserRepository userRepository,
            IQuestionResponseRepository questionResponseRepository) {
        this.testRepository = testRepository;
        this.testPartRepository = testPartRepository;
        this.testQuestionDetailRepository = testQuestionDetailRepository;
        this.testQuestionSetDetailRepository = testQuestionSetDetailRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.userRepository = userRepository;
        this.questionResponseRepository = questionResponseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PracticeTestInfoVM getPracticeTestInfo(UUID testId) {
        // Fetch test with necessary relationships
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + testId));

        // Calculate statistics
        int questionCount;
        int sectionCount = 0;

        if (test.getTestParts() == null || test.getTestParts().isEmpty()) {
            // If test has no parts, count questions from TestQuestionDetail and
            // TestQuestionSetDetail
            int directQuestions = testQuestionDetailRepository.countByTestId(testId);
            int questionSetQuestions = testQuestionDetailRepository.countQuestionsInQuestionSetsByTestId(testId);
            questionCount = directQuestions + questionSetQuestions;
        } else {
            // If test has parts, count questions from TestPartDetail
            questionCount = testQuestionDetailRepository.countQuestionsInTestPartsByTestId(testId);
            sectionCount = test.getTestParts().size();
        }
        int practicedUserCount = testAttemptRepository.countDistinctUsersByTestId(testId);

        int commentCount = 0; // Placeholder for comment count

        // Get skills from testSkills relationship (null-safe)
        List<String> skills = new ArrayList<>();
        if (test.getTestSkills() != null) {
            skills = test.getTestSkills().stream()
                    .map(testSkill -> testSkill != null && testSkill.getSkill() != null ? testSkill.getSkill().getName()
                            : null)
                    .filter(name -> name != null && !name.isBlank())
                    .distinct()
                    .collect(Collectors.toList());
        }

        // If no skills found, use the legacy skill field as fallback
        if (skills.isEmpty() && test.getSkill() != null && !test.getSkill().isEmpty()) {
            skills = List.of(test.getSkill());
        }

        // Build test part info list
        List<TestPartInfoVM> testPartInfoList = new ArrayList<>();
        List<TestPart> parts = testPartRepository.findByTestIdWithParts(testId);
        if (parts == null) {
            parts = new ArrayList<>();
        }
        for (TestPart tp : parts) {
            // Title from Part name; fallback to "Part {orderIndex}"
            String title = tp.getPart() != null && tp.getPart().getName() != null
                    ? tp.getPart().getName()
                    : ("Part " + (tp.getOrderIndex() != null ? tp.getOrderIndex() : 0));

            // Compute question count: direct questions + items from question sets
            int directCount = tp.getTestPartQuestions() != null
                    ? (int) tp.getTestPartQuestions().stream()
                            .filter(Objects::nonNull)
                            .filter(tpq -> tpq.getQuestion() != null
                                    && Boolean.FALSE.equals(tpq.getQuestion().getIsDeleted()))
                            .count()
                    : 0;

            int setItemCount = 0;
            if (tp.getTestPartQuestionSets() != null) {
                for (TestPartQuestionSet tqs : tp.getTestPartQuestionSets()) {
                    if (tqs != null && tqs.getQuestionSet() != null
                            && tqs.getQuestionSet().getQuestionSetItems() != null) {
                        setItemCount += (int) tqs.getQuestionSet().getQuestionSetItems().stream()
                                .filter(Objects::nonNull)
                                .filter(qsi -> qsi.getQuestion() != null
                                        && Boolean.FALSE.equals(qsi.getQuestion().getIsDeleted()))
                                .count();
                    }
                }
            }
            int partQuestionCount = directCount + setItemCount;

            // Collect distinct category names from questions and question set items
            List<String> categories = new ArrayList<>();
            if (tp.getTestPartQuestions() != null) {
                for (TestPartQuestion tpq : tp.getTestPartQuestions()) {
                    if (tpq != null && tpq.getQuestion() != null && tpq.getQuestion().getCategory() != null
                            && tpq.getQuestion().getCategory().getName() != null) {
                        categories.add(tpq.getQuestion().getCategory().getName());
                    }
                }
            }
            if (tp.getTestPartQuestionSets() != null) {
                for (TestPartQuestionSet tqs : tp.getTestPartQuestionSets()) {
                    if (tqs.getQuestionSet() != null && tqs.getQuestionSet().getQuestionSetItems() != null) {
                        tqs.getQuestionSet().getQuestionSetItems().forEach(item -> {
                            if (item != null && item.getQuestion() != null && item.getQuestion().getCategory() != null
                                    && item.getQuestion().getCategory().getName() != null) {
                                categories.add(item.getQuestion().getCategory().getName());
                            }
                        });
                    }
                }
            }
            List<String> distinctCategories = categories.stream().distinct().collect(Collectors.toList());

            // Set part ID and order
            UUID partId = (tp.getPart() != null) ? tp.getPart().getId() : null;
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
        testInfo.setTestId(testId);
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
        // Return the note from the test if it exists, otherwise return a default
        // message
        return test.getNote() != null && !test.getNote().isBlank()
                ? test.getNote()
                : "";
    }

    @Override
    public String getAllTests() {
        // TODO: Implement this method
        return null;
    }

    @Override
    public List<TestAttemptInfoVM> getTestAttempts(UUID testId, UUID userId, String tz) {
        // First try to get user ID from security context if not provided
        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User userDetails = 
                    (User) authentication.getPrincipal();
                try {
                    userId = userRepository.findByUsername(userDetails.getUsername()).getId();
                } catch (IllegalArgumentException e) {
                    // If username is not a valid UUID, return empty list
                    return Collections.emptyList();
                }
            } else {
                // If no authentication or invalid principal, return empty list
                return Collections.emptyList();
            }
        }
        
        // Now fetch attempts with the resolved user ID and sort by creation date (newest first)
        List<TestAttempt> attempts = testAttemptRepository.findByTestIdAndUserId(testId, userId).stream()
                .sorted(Comparator.comparing(TestAttempt::getInsertedAt).reversed())
                .collect(Collectors.toList());

        ZoneId zone = resolveZoneId(tz);

        return attempts.stream()
                .map(attempt -> mapToTestAttemptInfoVM(attempt, zone))
                .collect(Collectors.toList());
    }

    private TestAttemptInfoVM mapToTestAttemptInfoVM(TestAttempt attempt, ZoneId zone) {
        TestAttemptInfoVM vm = new TestAttemptInfoVM();
        // Expose the attempt identifier
        vm.setId(attempt.getId());
        Instant inserted = attempt.getInsertedAt();
        vm.setTakeDate(inserted);
        if (inserted != null) {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(inserted, zone);
            vm.setTakeDateLocal(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(zdt));
        }
        vm.setIsPractice(attempt.getIsPractice());
        vm.setStartTime(attempt.getStartTime());
        vm.setEndTime(attempt.getEndTime());
        vm.setDurationSeconds(attempt.getDurationSeconds());

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

        List<UUID> partIds = attempt.getTestPartAttempts().stream()
                .map(partAttempt -> {
                    // Get the TestPart for this attempt's part
                    TestPart testPart = partAttempt.getPart().getTestParts().stream()
                            .filter(tp -> tp.getTest().equals(attempt.getTest()))
                            .findFirst()
                            .orElse(null);
                    return testPart != null ? 
                            Map.entry(testPart.getOrderIndex(), partAttempt.getPart().getId()) : 
                            null;
                })
                .filter(Objects::nonNull)
                .sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        vm.setPartIds(partIds);

        // Calculate total questions based on practice mode
        int totalQuestions;
        if (attempt.getIsPractice() && !attempt.getTestPartAttempts().isEmpty()) {
            // For practice mode with part attempts, count questions in selected parts
            totalQuestions = calculateTotalQuestionsFromParts(attempt.getTest(), attempt.getTestPartAttempts());
        } else {
            // For non-practice mode or practice mode without part attempts, count all
            // questions
            totalQuestions = calculateTotalQuestionsFromTest(attempt.getTest());
        }

        vm.setTotalQuestions(totalQuestions);
        // Count correct answers for this test attempt
        int correctAnswers = questionResponseRepository.countByTestAttemptAndIsCorrect(attempt, true);
        vm.setCorrectAnswers(correctAnswers);

        return vm;
    }

    private ZoneId resolveZoneId(String tz) {
        try {
            if (tz != null && !tz.isBlank()) {
                return ZoneId.of(tz);
            }
        } catch (Exception ignored) {
            // Fallback to system default if invalid tz is provided
        }
        return ZoneId.systemDefault();
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

    @Override
    @Transactional(readOnly = true)
    public Page<TestListItemVM> getTestList(UUID testCategoryId, String keyword, Pageable pageable) {
        // Fetch tests with pagination, optional category filter, and keyword search
        Page<Test> testPage = testRepository.findByTestCategoryIdAndKeyword(testCategoryId, keyword, pageable);

        // Map each test to TestListItemVM
        List<TestListItemVM> testListItems = testPage.getContent().stream()
                .map(this::mapToTestListItemVM)
                .collect(Collectors.toList());

        return new PageImpl<>(testListItems, pageable, testPage.getTotalElements());
    }

    private TestListItemVM mapToTestListItemVM(Test test) {
        TestListItemVM vm = new TestListItemVM();
        
        // Set test ID
        vm.setId(test.getId());
        
        // Get skills from testSkills relationship (null-safe)
        List<String> skills = new ArrayList<>();
        if (test.getTestSkills() != null) {
            skills = test.getTestSkills().stream()
                    .map(testSkill -> testSkill != null && testSkill.getSkill() != null ? testSkill.getSkill().getName()
                            : null)
                    .filter(name -> name != null && !name.isBlank())
                    .distinct()
                    .collect(Collectors.toList());
        }
        
        // If no skills found, use the legacy skill field as fallback
        if (skills.isEmpty() && test.getSkill() != null && !test.getSkill().isEmpty()) {
            skills = List.of(test.getSkill());
        }
        vm.setSkills(skills);
        
        // Set test name
        vm.setTestName(test.getName());
        
        // Set duration
        vm.setDuration(calculateTestDuration(test) + " phút");
        
        // Calculate sections count
        int sectionCount = 0;
        if (test.getTestParts() != null && !test.getTestParts().isEmpty()) {
            sectionCount = test.getTestParts().size();
        }
        vm.setSections(sectionCount);
        
        // Calculate questions count
        int questionCount;
        if (test.getTestParts() == null || test.getTestParts().isEmpty()) {
            // If test has no parts, count questions from TestQuestionDetail and TestQuestionSetDetail
            int directQuestions = testQuestionDetailRepository.countByTestId(test.getId());
            int questionSetQuestions = testQuestionDetailRepository.countQuestionsInQuestionSetsByTestId(test.getId());
            questionCount = directQuestions + questionSetQuestions;
        } else {
            // If test has parts, count questions from TestPartDetail
            questionCount = testQuestionDetailRepository.countQuestionsInTestPartsByTestId(test.getId());
        }
        vm.setQuestions(questionCount);
        
        // Get practiced users count
        int practicedUserCount = testAttemptRepository.countDistinctUsersByTestId(test.getId());
        vm.setPracticedUsers(practicedUserCount);
        
        // Comments count (placeholder)
        vm.setComments(0);
        
        // Set note
        vm.setNote(generateTestNote(test));
        
        return vm;
    }
}
