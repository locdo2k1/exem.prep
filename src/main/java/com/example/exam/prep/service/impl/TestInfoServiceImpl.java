package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.Test;
import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.model.TestPart;
import com.example.exam.prep.model.TestPartAttempt;
import com.example.exam.prep.model.TestPartQuestion;
import com.example.exam.prep.model.TestPartQuestionSet;
import com.example.exam.prep.model.viewmodels.PracticeTestInfoVM;
import com.example.exam.prep.model.viewmodels.TestAttemptInfoVM;
import com.example.exam.prep.repository.ITestAttemptRepository;
import com.example.exam.prep.repository.ITestQuestionDetailRepository;
import com.example.exam.prep.repository.ITestRepository;
import com.example.exam.prep.repository.ITestPartRepository;
import com.example.exam.prep.service.ITestInfoService;
import com.example.exam.prep.viewmodel.TestPartInfoVM;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
@Service
@Transactional
public class TestInfoServiceImpl implements ITestInfoService {
    private final ITestRepository testRepository;
    private final ITestPartRepository testPartRepository;
    private final ITestQuestionDetailRepository testQuestionDetailRepository;
    private final ITestAttemptRepository testAttemptRepository;

    public TestInfoServiceImpl(
            ITestRepository testRepository,
            ITestPartRepository testPartRepository,
            ITestQuestionDetailRepository testQuestionDetailRepository,
            ITestAttemptRepository testAttemptRepository) {
        this.testRepository = testRepository;
        this.testPartRepository = testPartRepository;
        this.testQuestionDetailRepository = testQuestionDetailRepository;
        this.testAttemptRepository = testAttemptRepository;
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
        List<TestAttempt> attempts;
        if (userId != null) {
            attempts = testAttemptRepository.findByTestIdAndUserId(testId, userId);
        } else {
            attempts = testAttemptRepository.findByTestId(testId);
        }

        ZoneId zone = resolveZoneId(tz);

        return attempts.stream()
                .map(attempt -> mapToTestAttemptInfoVM(attempt, zone))
                .collect(Collectors.toList());
    }

    private TestAttemptInfoVM mapToTestAttemptInfoVM(TestAttempt attempt, ZoneId zone) {
        TestAttemptInfoVM vm = new TestAttemptInfoVM();
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

        // Get parts
        List<String> parts = attempt.getTestPartAttempts().stream()
                .map(partAttempt -> partAttempt.getPart().getName())
                .collect(Collectors.toList());
        vm.setParts(parts);

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
        // TODO: Implement correct answers calculation once the answer tracking is
        // implemented
        vm.setCorrectAnswers(0); // Placeholder

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
        return test.getTestParts().stream()
                .mapToInt(testPart -> testPart.getTestPartQuestions().size() +
                        testPart.getTestPartQuestionSets().stream()
                                .mapToInt(questionSet -> questionSet.getQuestionSet().getQuestions().size())
                                .sum())
                .sum();
    }
}
