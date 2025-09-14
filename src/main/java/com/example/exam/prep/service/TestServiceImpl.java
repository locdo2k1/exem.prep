package com.example.exam.prep.service;

import com.example.exam.prep.model.*;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.example.exam.prep.model.viewmodels.question.QuestionTypeViewModel;
import com.example.exam.prep.repository.ITestPartRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.exam.prep.model.viewmodels.file.FileInfoViewModel;
import com.example.exam.prep.viewmodel.test.answer.*;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import com.example.exam.prep.model.viewmodels.option.OptionViewModel;
import com.example.exam.prep.model.viewmodels.question.QuestionCategoryViewModel;
import com.example.exam.prep.vm.test.*;

@Service
@Transactional
public class TestServiceImpl implements ITestService {
    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<TestVM> getAllTests(org.springframework.data.domain.Pageable pageable,
            String search) {
        var testRepo = unitOfWork.getTestRepository();
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return testRepo.searchTests(searchTerm, pageable).map(TestVM::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<TestVMSimple> getAllTestsSimple(
            org.springframework.data.domain.Pageable pageable, String search) {
        var testRepo = unitOfWork.getTestRepository();
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return testRepo.searchTests(searchTerm, pageable).map(com.example.exam.prep.vm.test.TestVMSimple::fromEntity);
    }

    private final IUnitOfWork unitOfWork;
    private final IFileStorageService fileStorageService;

    public TestServiceImpl(IUnitOfWork unitOfWork, IFileStorageService fileStorageService) {
        this.unitOfWork = unitOfWork;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public Test editTest(TestEditVM testVM, List<MultipartFile> files) throws IOException {
        // Find existing test
        Test test = unitOfWork.getTestRepository().findById(testVM.getId())
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + testVM.getId()));

        // Clear existing relationships - cascading will handle the deletes
        if (test.getTestParts() != null) {
            test.getTestParts().clear();
        }
        if (test.getTestFiles() != null) {
            test.getTestFiles().clear();
        }
        if (test.getTestQuestionDetails() != null) {
            test.getTestQuestionDetails().clear();
        }
        if (test.getTestQuestionSetDetails() != null) {
            test.getTestQuestionSetDetails().clear();
        }

        // Save to ensure cascading deletes take effect
        unitOfWork.getTestRepository().saveAndFlush(test);

        // Clear test skills separately as they're not part of the cascading
        // relationship
        List<TestSkill> testSkills = unitOfWork.getTestSkillRepository().findByTestId(test.getId());
        if (testSkills != null && !testSkills.isEmpty()) {
            unitOfWork.getTestSkillRepository().deleteAll(testSkills);
        }

        // Update test properties
        test.setName(testVM.getTitle());
        test.setDescription(""); // Description is not available in TestEditVM, using empty string

        // Set test category if provided
        if (testVM.getTestCategoryId() != null) {
            TestCategory testCategory = unitOfWork.getTestCategoryRepository().findById(testVM.getTestCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Test category not found with id: " + testVM.getTestCategoryId()));
            test.setTestCategory(testCategory);
        } else {
            test.setTestCategory(null);
        }

        Test updatedTest = unitOfWork.getTestRepository().save(test);

        // Handle file uploads if any
        if (files != null && !files.isEmpty()) {
            // Filter out empty files
            List<MultipartFile> nonEmptyFiles = files.stream()
                    .filter(file -> !file.isEmpty())
                    .collect(Collectors.toList());

            if (!nonEmptyFiles.isEmpty()) {
                // Upload files using the file storage service
                String uploadPath = "tests/" + updatedTest.getId().toString();
                List<FileInfo> uploadedFiles = fileStorageService.uploadFiles(nonEmptyFiles, uploadPath);

                // Create TestFile relationships
                for (FileInfo fileInfo : uploadedFiles) {
                    TestFile testFile = new TestFile(updatedTest, fileInfo);
                    unitOfWork.getTestFileRepository().save(testFile);
                }
            }
        }

        // Handle skills
        if (testVM.getSkillIds() != null && !testVM.getSkillIds().isEmpty()) {
            List<Skill> skills = unitOfWork.getSkillRepository().findAllById(testVM.getSkillIds());
            if (skills.size() != testVM.getSkillIds().size()) {
                throw new IllegalArgumentException("One or more skills not found");
            }

            // Create TestSkill entities for each skill
            for (Skill skill : skills) {
                TestSkill testSkill = new TestSkill(updatedTest, skill);
                unitOfWork.getTestSkillRepository().save(testSkill);
            }
        }

        // Handle test parts and their questions
        if (testVM.getListPart() != null) {
            testVM.getListPart().forEach(partVM -> {
                TestPart testPart = createAndSaveTestPart(partVM, updatedTest);
                handlePartQuestions(partVM, testPart);
            });
        }

        // Handle question sets
        handleQuestionSets(testVM, updatedTest);

        // Handle individual questions
        handleIndividualQuestions(testVM, updatedTest);

        return updatedTest;
    }

    @Override
    @Transactional
    public void deleteTest(UUID id) {
        // First, load the test with all its relationships
        Optional<Test> testOpt = unitOfWork.getTestRepository().findByIdWithRelations(id);
        if (testOpt.isEmpty()) {
            throw new EntityNotFoundException("Test not found with id: " + id);
        }
        Test test = testOpt.get();

        try {
            // Delete test parts first
            if (test.getTestParts() != null && !test.getTestParts().isEmpty()) {
                // Create a new list to avoid concurrent modification
                List<TestPart> partsToDelete = new ArrayList<>(test.getTestParts());
                for (TestPart part : partsToDelete) {
                    unitOfWork.getTestPartRepository().delete(part);
                }
                test.getTestParts().clear();
                unitOfWork.getTestPartRepository().flush(); // Flush to ensure deletes are processed
            }

            // Delete test files
            if (test.getTestFiles() != null && !test.getTestFiles().isEmpty()) {
                unitOfWork.getTestFileRepository().deleteAll(test.getTestFiles());
                test.getTestFiles().clear();
                unitOfWork.getTestFileRepository().flush();
            }

            // Delete test question details
            if (test.getTestQuestionDetails() != null && !test.getTestQuestionDetails().isEmpty()) {
                unitOfWork.getTestQuestionDetailRepository().deleteAll(test.getTestQuestionDetails());
                test.getTestQuestionDetails().clear();
                unitOfWork.getTestQuestionDetailRepository().flush();
            }

            // Delete test question set details
            if (test.getTestQuestionSetDetails() != null && !test.getTestQuestionSetDetails().isEmpty()) {
                unitOfWork.getTestQuestionSetDetailRepository().deleteAll(test.getTestQuestionSetDetails());
                test.getTestQuestionSetDetails().clear();
                unitOfWork.getTestQuestionSetDetailRepository().flush();
            }

            // Delete test skills
            if (test.getTestSkills() != null && !test.getTestSkills().isEmpty()) {
                unitOfWork.getTestSkillRepository().deleteAll(test.getTestSkills());
                test.getTestSkills().clear();
                unitOfWork.getTestSkillRepository().flush();
            }

            // Delete the test itself
            unitOfWork.getTestRepository().delete(test);
            unitOfWork.getTestRepository().flush();

        } catch (Exception e) {
            // Log the error and rethrow
            throw new RuntimeException("Error deleting test with id: " + id, e);
        }
    }

    @Override
    @Transactional
    public Test createTest(TestCreateVM testVM, List<MultipartFile> files) throws IOException {
        // Create and save the test
        Test test = new Test();
        test.setName(testVM.getTitle());
        test.setDescription(""); // Description is not available in TestCreateVM, using empty string

        // Set test category if provided
        if (testVM.getTestCategoryId() != null) {
            TestCategory testCategory = unitOfWork.getTestCategoryRepository().findById(testVM.getTestCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Test category not found with id: " + testVM.getTestCategoryId()));
            test.setTestCategory(testCategory);
        }

        Test savedTest = unitOfWork.getTestRepository().save(test);

        // Handle file uploads if any
        if (files != null && !files.isEmpty()) {
            // Filter out empty files
            List<MultipartFile> nonEmptyFiles = files.stream()
                    .filter(file -> !file.isEmpty())
                    .collect(Collectors.toList());

            if (!nonEmptyFiles.isEmpty()) {
                // Upload files using the file storage service
                String uploadPath = "tests/" + savedTest.getId().toString();
                List<FileInfo> uploadedFiles = fileStorageService.uploadFiles(nonEmptyFiles, uploadPath);

                // Create TestFile relationships
                for (FileInfo fileInfo : uploadedFiles) {
                    TestFile testFile = new TestFile(savedTest, fileInfo);
                    unitOfWork.getTestFileRepository().save(testFile);
                }
            }
        }

        // Handle skills
        if (testVM.getSkillIds() != null && !testVM.getSkillIds().isEmpty()) {
            List<Skill> skills = unitOfWork.getSkillRepository().findAllById(testVM.getSkillIds());
            if (skills.size() != testVM.getSkillIds().size()) {
                throw new IllegalArgumentException("One or more skills not found");
            }

            // Create TestSkill entities for each skill
            for (Skill skill : skills) {
                TestSkill testSkill = new TestSkill(savedTest, skill);
                unitOfWork.getTestSkillRepository().save(testSkill);
            }
        }

        // Handle test parts and their questions
        if (testVM.getListPart() != null) {
            testVM.getListPart().forEach(partVM -> {
                TestPart testPart = createAndSaveTestPart(partVM, savedTest);
                handlePartQuestions(partVM, testPart);
            });
        }

        // Handle question sets
        handleQuestionSets(testVM, savedTest);

        // Handle individual questions
        handleIndividualQuestions(testVM, savedTest);

        return savedTest;
    }

    private TestPart createAndSaveTestPart(TestPartVM partVM, Test test) {
        // Find the existing part by ID
        Part part = unitOfWork.getPartRepository().findById(partVM.getId())
                .orElseThrow(() -> new IllegalArgumentException("Part not found with id: " + partVM.getId()));

        // Create a new TestPart to link the test and part with order
        TestPart testPart = new TestPart(test, part, partVM.getOrder());

        // Initialize the collections to avoid null pointer exceptions
        testPart.setTestPartQuestions(new HashSet<>());
        testPart.setTestPartQuestionSets(new HashSet<>());

        return unitOfWork.getTestPartRepository().save(testPart);
    }

    private void handlePartQuestions(TestPartVM partVM, TestPart testPart) {
        // Handle question sets for this test part if any
        if (partVM.getQuestionSets() != null && !partVM.getQuestionSets().isEmpty()) {
            int order = 1;
            for (TestQuestionSetVM questionSetVM : partVM.getQuestionSets()) {
                QuestionSet questionSet = unitOfWork.getQuestionSetRepository()
                        .findById(questionSetVM.getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Question set not found with id: " + questionSetVM.getId()));

                TestPartQuestionSet testPartQuestionSet = new TestPartQuestionSet(
                        testPart,
                        questionSet,
                        questionSetVM.getOrder() != null ? questionSetVM.getOrder() : order++);

                testPart.getTestPartQuestionSets().add(testPartQuestionSet);
            }
        }

        // Handle individual questions for this test part if any
        if (partVM.getQuestions() != null && !partVM.getQuestions().isEmpty()) {
            int order = 1;
            for (TestQuestionVM questionVM : partVM.getQuestions()) {
                Question question = unitOfWork.getQuestionRepository()
                        .findById(questionVM.getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Question not found with id: " + questionVM.getId()));

                TestPartQuestion testPartQuestion = new TestPartQuestion(
                        testPart,
                        question,
                        questionVM.getOrder() != null ? questionVM.getOrder() : order++);

                testPart.getTestPartQuestions().add(testPartQuestion);
            }
        }

        // Save the updated test part with its relationships
        unitOfWork.getTestPartRepository().save(testPart);
    }

    @Override
    @Transactional(readOnly = true)
    public TestVM findById(UUID id) {
        // Fetch the test with necessary relationships
        Test test = unitOfWork.getTestRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + id));

        // Create the base VM
        TestVM vm = TestVM.fromEntity(test);

        // Set isActive (using getter method if exists, otherwise default to true)
        try {
            vm.setActive((boolean) test.getClass().getMethod("isActive").invoke(test));
        } catch (Exception e) {
            vm.setActive(true); // Default to true if isActive method doesn't exist
        }

        // Initialize lists
        List<TestQuestionSetVM> allQuestionSets = new ArrayList<>();
        List<TestQuestionItemVM> allQuestionItems = new ArrayList<>();

        // Fetch and set test parts
        if (test.getTestParts() != null && !test.getTestParts().isEmpty()) {
            List<TestPartDetailVM> partVMs = test.getTestParts().stream()
                    .sorted(Comparator.comparing(TestPart::getOrderIndex))
                    .map(part -> {
                        TestPartDetailVM partVM = new TestPartDetailVM();
                        partVM.setId(part.getPart().getId());
                        partVM.setTitle(part.getPart() != null ? part.getPart().getName() : "");
                        partVM.setDescription(part.getPart() != null ? part.getPart().getDescription() : "");
                        partVM.setOrder(part.getOrderIndex());

                        // Map questions in this part using TestPartQuestion
                        if (part.getTestPartQuestions() != null) {
                            part.getTestPartQuestions().stream()
                                    .filter(detail -> detail.getQuestion() != null)
                                    .sorted(Comparator.comparing(TestPartQuestion::getDisplayOrder))
                                    .forEach(testPartQuestion -> {
                                        Question question = testPartQuestion.getQuestion();
                                        TestQuestionVM questionVM = new TestQuestionVM();
                                        questionVM.setId(question.getId());
                                        questionVM.setPrompt(question.getPrompt());
                                        questionVM.setPartId(part.getId());
                                        questionVM.setQuestionCategory(
                                                convertToQuestionCategoryViewModel(question.getCategory()));
                                        questionVM.setQuestionType(
                                                convertToQuestionTypeViewModel(question.getQuestionType()));
                                        questionVM.setScore(question.getScore());
                                        questionVM.setOrder(testPartQuestion.getDisplayOrder());

                                        // Map fillBlankAnswers to questionAnswers if they exist
                                        List<String> answers = question.getFillBlankAnswers() != null
                                                ? question.getFillBlankAnswers().stream()
                                                        .map(FillBlankAnswer::getAnswerText)
                                                        .collect(Collectors.toList())
                                                : Collections.emptyList();
                                        questionVM.setQuestionAnswers(answers);

                                        // Map options
                                        List<OptionViewModel> optionVMs = question.getOptions() != null
                                                ? question.getOptions().stream()
                                                        .map(option -> {
                                                            OptionViewModel optionVM = new OptionViewModel();
                                                            optionVM.setId(option.getId());
                                                            optionVM.setText(option.getText());
                                                            optionVM.setCorrect(option.isCorrect());
                                                            return optionVM;
                                                        })
                                                        .collect(Collectors.toList())
                                                : Collections.emptyList();
                                        questionVM.setOptions(optionVMs);

                                        // Map file infos
                                        List<FileInfoViewModel> fileInfoViewModels = question.getFileInfos() != null
                                                ? question.getFileInfos().stream()
                                                        .map(file -> {
                                                            FileInfoViewModel fileViewModel = new FileInfoViewModel();
                                                            fileViewModel.setId(file.getId());
                                                            fileViewModel.setFileName(file.getFileName());
                                                            fileViewModel.setFileUrl(file.getUrl());
                                                            fileViewModel.setFileType(file.getFileType());
                                                            fileViewModel.setFileSize(file.getFileSize());
                                                            return fileViewModel;
                                                        })
                                                        .collect(Collectors.toList())
                                                : Collections.emptyList();
                                        questionVM.setQuestionAudios(fileInfoViewModels);

                                        TestQuestionItemVM itemVM = new TestQuestionItemVM();
                                        itemVM.setQuestion(questionVM);
                                        itemVM.setOrder(questionVM.getOrder());
                                        allQuestionItems.add(itemVM);
                                        partVM.getQuestionItems().add(itemVM);
                                    });
                        }

                        // Map question sets in this part using TestPartQuestionSet
                        if (part.getTestPartQuestionSets() != null) {
                            part.getTestPartQuestionSets().stream()
                                    .filter(detail -> detail.getQuestionSet() != null)
                                    .sorted(Comparator.comparing(TestPartQuestionSet::getDisplayOrder))
                                    .forEach(testPartQuestionSet -> {
                                        QuestionSet questionSet = testPartQuestionSet.getQuestionSet();
                                        TestQuestionSetVM setVM = new TestQuestionSetVM();
                                        setVM.setId(questionSet.getId());
                                        setVM.setTitle(questionSet.getTitle());
                                        setVM.setDescription(questionSet.getDescription());
                                        setVM.setImageUrl(questionSet.getImageUrl());
                                        setVM.setPartId(part.getId());
                                        setVM.setOrder(testPartQuestionSet.getDisplayOrder());

                                        // Map questions in this question set
                                        if (questionSet.getQuestionSetItems() != null) {
                                            List<TestQuestionVM> questionVMs = questionSet.getQuestionSetItems()
                                                    .stream()
                                                    .filter(item -> item.getQuestion() != null)
                                                    .sorted(Comparator.comparing(QuestionSetItem::getOrder))
                                                    .map(item -> {
                                                        Question question = item.getQuestion();
                                                        TestQuestionVM questionVM = new TestQuestionVM();
                                                        questionVM.setId(question.getId());
                                                        questionVM.setPrompt(question.getPrompt());
                                                        questionVM.setPartId(part.getId());
                                                        questionVM.setQuestionType(convertToQuestionTypeViewModel(
                                                                question.getQuestionType()));
                                                        questionVM.setScore(question.getScore());
                                                        questionVM.setOrder(setVM.getOrder() - 1 + item.getOrder());

                                                        // Map fillBlankAnswers to questionAnswers if they exist
                                                        List<String> answers = question.getFillBlankAnswers() != null
                                                                ? question.getFillBlankAnswers().stream()
                                                                        .map(FillBlankAnswer::getAnswerText)
                                                                        .collect(Collectors.toList())
                                                                : Collections.emptyList();
                                                        questionVM.setQuestionAnswers(answers);

                                                        // Map options
                                                        List<OptionViewModel> optionVMs = question.getOptions() != null
                                                                ? question.getOptions().stream()
                                                                        .map(option -> {
                                                                            OptionViewModel optionVM = new OptionViewModel();
                                                                            optionVM.setId(option.getId());
                                                                            optionVM.setText(option.getText());
                                                                            optionVM.setCorrect(option.isCorrect());
                                                                            return optionVM;
                                                                        })
                                                                        .collect(Collectors.toList())
                                                                : Collections.emptyList();
                                                        questionVM.setOptions(optionVMs);

                                                        // Map file infos
                                                        List<FileInfoViewModel> fileInfoViewModels = question
                                                                .getFileInfos() != null
                                                                        ? question.getFileInfos().stream()
                                                                                .map(file -> {
                                                                                    FileInfoViewModel fileViewModel = new FileInfoViewModel();
                                                                                    fileViewModel.setId(file.getId());
                                                                                    fileViewModel.setFileName(
                                                                                            file.getFileName());
                                                                                    fileViewModel
                                                                                            .setFileUrl(file.getUrl());
                                                                                    fileViewModel.setFileType(
                                                                                            file.getFileType());
                                                                                    fileViewModel.setFileSize(
                                                                                            file.getFileSize());
                                                                                    return fileViewModel;
                                                                                })
                                                                                .collect(Collectors.toList())
                                                                        : Collections.emptyList();
                                                        questionVM.setQuestionAudios(fileInfoViewModels);
                                                        return questionVM;
                                                    })
                                                    .collect(Collectors.toList());
                                            setVM.setQuestions(questionVMs);
                                            setVM.setTotalQuestions(questionVMs.size());
                                            setVM.setTotalScore(
                                                    questionVMs.stream().mapToInt(TestQuestionVM::getScore).sum());
                                        }

                                        TestQuestionItemVM itemVM = new TestQuestionItemVM();
                                        itemVM.setQuestionSet(setVM);
                                        itemVM.setOrder(setVM.getOrder());
                                        allQuestionSets.add(setVM);
                                        partVM.getQuestionItems().add(itemVM);
                                    });
                        }

                        return partVM;
                    })
                    .collect(Collectors.toList());
            vm.setListPart(partVMs);
        }

        // Map individual questions from testQuestionDetails
        if (test.getTestQuestionDetails() != null) {
            test.getTestQuestionDetails().stream()
                    .filter(detail -> detail.getQuestion() != null)
                    .sorted(Comparator.comparing(TestQuestionDetail::getOrder))
                    .forEach(detail -> {
                        Question question = detail.getQuestion();
                        TestQuestionVM questionVM = new TestQuestionVM();
                        questionVM.setId(question.getId());
                        questionVM.setPrompt(question.getPrompt());
                        questionVM.setQuestionType(convertToQuestionTypeViewModel(question.getQuestionType()));
                        questionVM.setScore(question.getScore());
                        questionVM.setOrder(detail.getOrder());

                        // Map fill blank answers if any
                        if (question.getFillBlankAnswers() != null && !question.getFillBlankAnswers().isEmpty()) {
                            List<String> answers = question.getFillBlankAnswers().stream()
                                    .map(FillBlankAnswer::getAnswerText)
                                    .collect(Collectors.toList());
                            questionVM.setQuestionAnswers(answers);
                        }

                        // Map options if any
                        if (question.getOptions() != null) {
                            List<OptionViewModel> optionVMs = question.getOptions().stream()
                                    .map(option -> {
                                        OptionViewModel optionVM = new OptionViewModel();
                                        optionVM.setId(option.getId());
                                        optionVM.setText(option.getText());
                                        optionVM.setCorrect(option.isCorrect());
                                        return optionVM;
                                    })
                                    .collect(Collectors.toList());
                            questionVM.setOptions(optionVMs);
                        }

                        // Map file infos if any
                        if (question.getFileInfos() != null) {
                            List<FileInfoViewModel> fileInfoViewModels = question.getFileInfos().stream()
                                    .map(fileInfo -> {
                                        FileInfoViewModel fileInfoVM = new FileInfoViewModel();
                                        fileInfoVM.setId(fileInfo.getId());
                                        fileInfoVM.setFileName(fileInfo.getFileName());
                                        fileInfoVM.setFileUrl(fileInfo.getUrl());
                                        fileInfoVM.setFileType(fileInfo.getFileType());
                                        fileInfoVM.setFileSize(fileInfo.getFileSize());
                                        return fileInfoVM;
                                    })
                                    .collect(Collectors.toList());
                            questionVM.setQuestionAudios(fileInfoViewModels);
                        }

                        TestQuestionItemVM itemVM = new TestQuestionItemVM();
                        itemVM.setQuestion(questionVM);
                        allQuestionItems.add(itemVM);
                    });
        }

        // Map question sets from testQuestionSetDetails
        if (test.getTestQuestionSetDetails() != null) {
            test.getTestQuestionSetDetails().stream()
                    .filter(detail -> detail.getQuestionSet() != null)
                    .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder))
                    .forEach(detail -> {
                        QuestionSet questionSet = detail.getQuestionSet();
                        TestQuestionSetVM setVM = new TestQuestionSetVM();
                        setVM.setId(questionSet.getId());
                        setVM.setTitle(questionSet.getTitle());
                        setVM.setDescription(questionSet.getDescription());
                        setVM.setOrder(detail.getOrder());
                        setVM.setImageUrl(questionSet.getImageUrl());

                        // Map questions in this question set
                        if (questionSet.getQuestionSetItems() != null) {
                            List<TestQuestionVM> questionVMs = questionSet.getQuestionSetItems().stream()
                                    .filter(QuestionSetItem::getIsActive)
                                    .filter(item -> item.getQuestion() != null)
                                    .sorted(Comparator.comparing(QuestionSetItem::getOrder,
                                            Comparator.nullsLast(Comparator.naturalOrder())))
                                    .map(item -> {
                                        Question question = item.getQuestion();
                                        TestQuestionVM questionVM = new TestQuestionVM();
                                        questionVM.setId(question.getId());
                                        questionVM.setPrompt(question.getPrompt());
                                        questionVM.setQuestionType(
                                                convertToQuestionTypeViewModel(question.getQuestionType()));
                                        questionVM.setScore(item.getCustomScore() != null ? item.getCustomScore()
                                                : question.getScore());
                                        questionVM.setOrder(
                                                item.getOrder() != null ? item.getOrder() - 1 + setVM.getOrder() : 0);

                                        // Map fill blank answers if any
                                        if (question.getFillBlankAnswers() != null
                                                && !question.getFillBlankAnswers().isEmpty()) {
                                            List<String> answers = question.getFillBlankAnswers().stream()
                                                    .map(FillBlankAnswer::getAnswerText)
                                                    .collect(Collectors.toList());
                                            questionVM.setQuestionAnswers(answers);
                                        }

                                        // Map options if any
                                        if (question.getOptions() != null) {
                                            List<OptionViewModel> optionVMs = question.getOptions().stream()
                                                    .map(option -> {
                                                        OptionViewModel optionVM = new OptionViewModel();
                                                        optionVM.setId(option.getId());
                                                        optionVM.setText(option.getText());
                                                        optionVM.setCorrect(option.isCorrect());
                                                        return optionVM;
                                                    })
                                                    .collect(Collectors.toList());
                                            questionVM.setOptions(optionVMs);
                                        }

                                        // Map file infos if any
                                        if (question.getFileInfos() != null) {
                                            List<FileInfoViewModel> fileInfoViewModels = question.getFileInfos()
                                                    .stream()
                                                    .map(fileInfo -> {
                                                        FileInfoViewModel fileInfoVM = new FileInfoViewModel();
                                                        fileInfoVM.setId(fileInfo.getId());
                                                        fileInfoVM.setFileName(fileInfo.getFileName());
                                                        fileInfoVM.setFileUrl(fileInfo.getUrl());
                                                        fileInfoVM.setFileType(fileInfo.getFileType());
                                                        fileInfoVM.setFileSize(fileInfo.getFileSize());
                                                        return fileInfoVM;
                                                    })
                                                    .collect(Collectors.toList());
                                            questionVM.setQuestionAudios(fileInfoViewModels);
                                        }

                                        return questionVM;
                                    })
                                    .collect(Collectors.toList());
                            setVM.setQuestions(questionVMs);
                            setVM.setTotalQuestions(questionVMs.size());
                            setVM.setTotalScore(questionVMs.stream()
                                    .mapToInt(TestQuestionVM::getScore)
                                    .sum());
                        } else {
                            setVM.setQuestions(Collections.emptyList());
                            setVM.setTotalQuestions(0);
                            setVM.setTotalScore(0);
                        }

                        TestQuestionItemVM itemVM = new TestQuestionItemVM();
                        itemVM.setQuestionSet(setVM);
                        allQuestionItems.add(itemVM);

                    });
        }

        vm.setListQuestionItem(allQuestionItems);

        // Fetch and set test skills
        List<TestSkill> testSkills = unitOfWork.getTestSkillRepository().findByTestId(test.getId());
        if (testSkills != null && !testSkills.isEmpty()) {
            List<TestSkillVM> skillVMs = testSkills.stream()
                    .map(testSkill -> {
                        Skill skill = testSkill.getSkill();
                        return TestSkillVM.builder()
                                .code(skill.getCode())
                                .name(skill.getName())
                                .description(skill.getDescription())
                                .build();
                    })
                    .collect(Collectors.toList());
            vm.setListSkill(skillVMs);
        }

        return vm;
    }

    private void handleQuestionSets(TestCreateVM testVM, Test savedTest) {
        if (testVM.getListQuestionSet() != null && !testVM.getListQuestionSet().isEmpty()) {
            testVM.getListQuestionSet().forEach(questionSetOrder -> {
                // Find the question set
                QuestionSet questionSet = unitOfWork.getQuestionSetRepository()
                        .findById(questionSetOrder.getQuestionSetId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Question set not found with id: " + questionSetOrder.getQuestionSetId()));

                // Create a test question set detail
                TestQuestionSetDetail testQuestionSetDetail = new TestQuestionSetDetail();
                testQuestionSetDetail.setTest(savedTest);
                testQuestionSetDetail.setQuestionSet(questionSet);
                testQuestionSetDetail.setOrder(questionSetOrder.getOrder());

                // Save the test question set detail
                unitOfWork.getTestQuestionSetDetailRepository().save(testQuestionSetDetail);
            });
        }
    }

    private QuestionTypeViewModel convertToQuestionTypeViewModel(QuestionType questionType) {
        if (questionType == null) {
            return null;
        }
        QuestionTypeViewModel viewModel = new QuestionTypeViewModel();
        viewModel.setId(questionType.getId());
        viewModel.setName(questionType.getName());
        // Map any other required fields from QuestionType to QuestionTypeViewModel
        return viewModel;
    }

    private QuestionCategoryViewModel convertToQuestionCategoryViewModel(QuestionCategory category) {
        if (category == null) {
            return null;
        }
        QuestionCategoryViewModel viewModel = new QuestionCategoryViewModel();
        viewModel.setId(category.getId());
        viewModel.setName(category.getName());
        // Add any other necessary field mappings here
        return viewModel;
    }

    private void handleIndividualQuestions(TestCreateVM testVM, Test savedTest) {
        if (testVM.getListQuestion() != null && !testVM.getListQuestion().isEmpty()) {
            testVM.getListQuestion().forEach(questionOrder -> {
                // Find the question
                Question question = unitOfWork.getQuestionRepository()
                        .findById(questionOrder.getQuestionId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Question not found with id: " + questionOrder.getQuestionId()));

                // Create a test question detail
                TestQuestionDetail testQuestionDetail = new TestQuestionDetail();
                testQuestionDetail.setTest(savedTest);
                testQuestionDetail.setQuestion(question);
                testQuestionDetail.setOrder(questionOrder.getOrder());

                // Save the test question detail
                unitOfWork.getTestQuestionDetailRepository().save(testQuestionDetail);
            });
        }
    }

    private TestAnswerPartVM getTestAnswerPartById(UUID partId, UUID testId) {
        // Get the test part that connects this part to the test
        TestPart testPart = unitOfWork.getTestPartRepository().findAll().stream()
                .filter(tp -> tp.getPart() != null && tp.getPart().getId().equals(partId)
                        && tp.getTest() != null && tp.getTest().getId().equals(testId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Test part not found for partId: " + partId + " and testId: " + testId));

        // Get the part with its relationships
        Part part = testPart.getPart();
        if (part == null) {
            throw new EntityNotFoundException("Part not found for testPart: " + testPart.getId());
        }

        // Create the view model
        TestAnswerPartVM partVM = new TestAnswerPartVM();
        partVM.setId(part.getId());
        partVM.setName(part.getName() != null ? part.getName() : "");

        // Initialize the list to hold both questions and question sets
        List<TestAnswerQuestionAndQuestionSetVM> questionsAndSets = new ArrayList<>();

        // Process individual questions in this test part
        if (testPart.getTestPartQuestions() != null) {
            testPart.getTestPartQuestions().stream()
                    .filter(Objects::nonNull)
                    .filter(tpq -> tpq.getQuestion() != null)
                    .sorted(Comparator.comparing(TestPartQuestion::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(testPartQuestion -> {
                        Question question = testPartQuestion.getQuestion();
                        TestAnswerQuestionVM questionVM = new TestAnswerQuestionVM();
                        // Set the order for the question
                        Integer displayOrder = testPartQuestion.getDisplayOrder();
                        questionVM.setOrder(displayOrder != null ? displayOrder : 0);

                        // Find and set the correct option if this is a multiple choice question
                        if (question.getOptions() != null) {
                            question.getOptions().stream()
                                    .filter(Option::isCorrect)
                                    .findFirst()
                                    .ifPresent(option -> {
                                        TestAnswerOptionVM optionVM = new TestAnswerOptionVM();
                                        optionVM.setText(option.getText());
                                        questionVM.setCorrectOption(optionVM);
                                    });
                        }

                        // Set transcript from question
                        questionVM.setTranscript(question.getTranscript());

                        // Set fillBlankAnswers as correctOption if this is a fill-in-the-blank question
                        if (question.getFillBlankAnswers() != null && !question.getFillBlankAnswers().isEmpty()) {
                            String answers = question.getFillBlankAnswers().stream()
                                    .map(FillBlankAnswer::getAnswerText)
                                    .collect(Collectors.joining(", "));
                            TestAnswerOptionVM optionVM = new TestAnswerOptionVM();
                            optionVM.setText(answers);
                            questionVM.setCorrectOption(optionVM);
                        }

                        TestAnswerQuestionAndQuestionSetVM vm = new TestAnswerQuestionAndQuestionSetVM();
                        vm.setId(question.getId());
                        vm.setOrder(displayOrder != null ? displayOrder : 0);
                        vm.setQuestion(questionVM);
                        questionsAndSets.add(vm);
                    });
        }

        // Process question sets in this test part
        if (testPart.getTestPartQuestionSets() != null) {
            testPart.getTestPartQuestionSets().stream()
                    .filter(Objects::nonNull)
                    .filter(tpqs -> tpqs.getQuestionSet() != null)
                    .sorted(Comparator.comparing(TestPartQuestionSet::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(testPartQuestionSet -> {
                        QuestionSet questionSet = testPartQuestionSet.getQuestionSet();
                        Integer displayOrder = testPartQuestionSet.getDisplayOrder();

                        // Create question set VM
                        TestAnswerQuestionSetVM setVM = new TestAnswerQuestionSetVM();
                        setVM.setOrder(displayOrder != null ? displayOrder : 0);

                        // Process questions in this question set
                        if (questionSet.getQuestionSetItems() != null) {
                            List<TestAnswerQuestionVM> questionVMs = questionSet.getQuestionSetItems().stream()
                                    .filter(QuestionSetItem::getIsActive)
                                    .filter(item -> item.getQuestion() != null)
                                    .sorted(Comparator.comparing(QuestionSetItem::getOrder,
                                            Comparator.nullsLast(Comparator.naturalOrder())))
                                    .map(item -> {
                                        Question question = item.getQuestion();
                                        TestAnswerQuestionVM qVM = new TestAnswerQuestionVM();
                                        qVM.setOrder(item.getOrder() != null ? item.getOrder() : 0);

                                        // Find and set the correct option if this is a multiple choice question
                                        if (question.getOptions() != null) {
                                            question.getOptions().stream()
                                                    .filter(Option::isCorrect)
                                                    .findFirst()
                                                    .ifPresent(option -> {
                                                        TestAnswerOptionVM optionVM = new TestAnswerOptionVM();
                                                        optionVM.setText(option.getText());
                                                        qVM.setCorrectOption(optionVM);
                                                    });
                                        }

                                        // Set transcript if available
                                        if (question.getFillBlankAnswers() != null &&
                                                !question.getFillBlankAnswers().isEmpty()) {
                                            String transcript = question.getFillBlankAnswers().stream()
                                                    .map(FillBlankAnswer::getAnswerText)
                                                    .collect(Collectors.joining(", "));
                                            qVM.setTranscript(transcript);
                                        }

                                        return qVM;
                                    })
                                    .collect(Collectors.toList());
                            setVM.setQuestions(questionVMs);
                        }

                        TestAnswerQuestionAndQuestionSetVM vm = new TestAnswerQuestionAndQuestionSetVM();
                        vm.setId(questionSet.getId());
                        vm.setOrder(displayOrder != null ? displayOrder : 0);
                        vm.setQuestionSet(setVM);
                        questionsAndSets.add(vm);
                    });
        }

        // Sort all items by their order
        questionsAndSets.sort(Comparator.comparingInt(TestAnswerQuestionAndQuestionSetVM::getOrder));
        partVM.setQuestionsAndQuestionSets(questionsAndSets);

        return partVM;
    }

    @Override
    public List<FlattenedQuestionVM> getFlattenedQuestionsForTestPart(UUID partId, UUID testId) {
        // Get the test part that connects this part to the test
        TestPart testPart = unitOfWork.getTestPartRepository().findAll().stream()
                .filter(tp -> tp.getPart() != null && tp.getPart().getId().equals(partId)
                        && tp.getTest() != null && tp.getTest().getId().equals(testId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Test part not found for partId: " + partId + " and testId: " + testId));

        // Initialize the list to hold flattened questions
        List<FlattenedQuestionVM> flattenedQuestions = new ArrayList<>();
        // Process individual questions in this test part
        if (testPart.getTestPartQuestions() != null) {
            testPart.getTestPartQuestions().stream()
                    .filter(Objects::nonNull)
                    .filter(tpq -> tpq.getQuestion() != null)
                    .sorted(Comparator.comparing(TestPartQuestion::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(testPartQuestion -> {
                        Question question = testPartQuestion.getQuestion();
                        FlattenedQuestionVM flatQuestion = new FlattenedQuestionVM();

                        // Set basic question info
                        flatQuestion.setOrder(testPartQuestion.getDisplayOrder());
                        flatQuestion.setPart(testPart.getPart().getName());

                        // Set correct answer
                        String correctAnswer = "";
                        if (question.getOptions() != null) {
                            correctAnswer = question.getOptions().stream()
                                    .filter(Option::isCorrect)
                                    .findFirst()
                                    .map(Option::getText)
                                    .orElse("");
                        } else if (question.getFillBlankAnswers() != null
                                && !question.getFillBlankAnswers().isEmpty()) {
                            correctAnswer = question.getFillBlankAnswers().stream()
                                    .map(FillBlankAnswer::getAnswerText)
                                    .collect(Collectors.joining(", "));
                        }
                        flatQuestion.setAnswer(correctAnswer);

                        // Set transcript and defaults
                        flatQuestion.setTranscript(question.getTranscript() != null ? question.getTranscript() : "");
                        flatQuestion.setShowTranscript(false);
                        flatQuestion.setCorrect(false);
                        flatQuestion.setUserAnswer("");

                        flattenedQuestions.add(flatQuestion);
                    });
        }

        // Process question sets in this test part
        if (testPart.getTestPartQuestionSets() != null) {
            testPart.getTestPartQuestionSets().stream()
                    .filter(Objects::nonNull)
                    .filter(tpqs -> tpqs.getQuestionSet() != null)
                    .sorted(Comparator.comparing(TestPartQuestionSet::getDisplayOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(testPartQuestionSet -> {
                        QuestionSet questionSet = testPartQuestionSet.getQuestionSet();

                        // Process each question in the question set
                        if (questionSet.getQuestionSetItems() != null) {
                            questionSet.getQuestionSetItems().stream()
                                    .filter(QuestionSetItem::getIsActive)
                                    .filter(item -> item.getQuestion() != null)
                                    .sorted(Comparator.comparing(QuestionSetItem::getOrder,
                                            Comparator.nullsLast(Comparator.naturalOrder())))
                                    .forEach(item -> {
                                        Question question = item.getQuestion();
                                        FlattenedQuestionVM flatQuestion = new FlattenedQuestionVM();

                                        // Set basic question info
                                        flatQuestion.setOrder(testPartQuestionSet.getDisplayOrder() - 1 + item.getOrder());
                                        flatQuestion
                                                .setPart(testPart.getPart().getName() != null ? testPart.getPart().getName() : "");

                                        // Set correct answer
                                        String correctAnswer = "";
                                        if (question.getOptions() != null) {
                                            correctAnswer = question.getOptions().stream()
                                                    .filter(Option::isCorrect)
                                                    .findFirst()
                                                    .map(Option::getText)
                                                    .orElse("");
                                        } else if (question.getFillBlankAnswers() != null
                                                && !question.getFillBlankAnswers().isEmpty()) {
                                            correctAnswer = question.getFillBlankAnswers().stream()
                                                    .map(FillBlankAnswer::getAnswerText)
                                                    .collect(Collectors.joining(", "));
                                        }
                                        flatQuestion.setAnswer(correctAnswer);

                                        // Set transcript and defaults
                                        flatQuestion.setTranscript(
                                                question.getTranscript() != null ? question.getTranscript() : "");
                                        flatQuestion.setShowTranscript(false);
                                        flatQuestion.setCorrect(false);
                                        flatQuestion.setUserAnswer("");

                                        flattenedQuestions.add(flatQuestion);
                                    });
                        }
                    });
        }

        return flattenedQuestions;
    }

    @Override
    public TestAnswerVM testAnswers(UUID testId) {
        // Get the test with its relationships
        Test test = unitOfWork.getTestRepository().findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + testId));
        List<FlattenedQuestionVM> flattenedQuestions = new ArrayList<>();

        // Create the view model
        TestAnswerVM vm = new TestAnswerVM();
        vm.setTestId(test.getId());
        vm.setTestName(test.getName());

        // Process test parts if they exist
        if (test.getTestParts() != null && !test.getTestParts().isEmpty()) {
            List<TestPart> testParts = test.getTestParts().stream()
                    .filter(Objects::nonNull)
                    .filter(tp -> tp.getPart() != null)
                    .sorted(Comparator.comparing(TestPart::getOrderIndex,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());

            // Process each test part to get its details and questions/question sets
            for (TestPart testPart : testParts) {
                UUID partId = testPart.getPart().getId();
                // Get the part details using the existing method
                List<FlattenedQuestionVM> flattenedQuestionsForTestPart = getFlattenedQuestionsForTestPart(partId,
                        testId);

                flattenedQuestions.addAll(flattenedQuestionsForTestPart);
            }
        }

        // Map individual questions from testQuestionDetails
        if (test.getTestQuestionDetails() != null) {
            test.getTestQuestionDetails().stream()
                    .filter(Objects::nonNull)
                    .filter(detail -> detail.getQuestion() != null)
                    .sorted(Comparator.comparing(TestQuestionDetail::getOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(detail -> {
                        Question question = detail.getQuestion();
                        int order = detail.getOrder() != null ? detail.getOrder() : 0;

                        FlattenedQuestionVM flattenedQuestion = new FlattenedQuestionVM();
                        flattenedQuestion.setOrder(order);
                        flattenedQuestion.setPart("");
                        flattenedQuestion.setTranscript(question.getTranscript());
                        flattenedQuestion.setShowTranscript(false);

                        // Set answer based on question type
                        if (question.getOptions() != null) {
                            // For multiple choice questions
                            question.getOptions().stream()
                                    .filter(Option::isCorrect)
                                    .findFirst()
                                    .ifPresent(option -> flattenedQuestion.setAnswer(option.getText()));
                        } else if (question.getFillBlankAnswers() != null
                                && !question.getFillBlankAnswers().isEmpty()) {
                            // For fill-in-the-blank questions
                            String answers = question.getFillBlankAnswers().stream()
                                    .map(FillBlankAnswer::getAnswerText)
                                    .collect(Collectors.joining(", "));
                            flattenedQuestion.setAnswer(answers);
                        }

                        flattenedQuestions.add(flattenedQuestion);
                    });
        }

        // Map question sets from testQuestionSetDetails
        if (test.getTestQuestionSetDetails() != null) {
            test.getTestQuestionSetDetails().stream()
                    .filter(Objects::nonNull)
                    .filter(detail -> detail.getQuestionSet() != null)
                    .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .flatMap(detail -> {
                        QuestionSet questionSet = detail.getQuestionSet();
                        int baseOrder = detail.getOrder() != null ? detail.getOrder() : 0;

                        return questionSet.getQuestionSetItems().stream()
                                .filter(QuestionSetItem::getIsActive)
                                .filter(item -> item.getQuestion() != null)
                                .sorted(Comparator.comparing(QuestionSetItem::getOrder,
                                        Comparator.nullsLast(Comparator.naturalOrder())))
                                .map(item -> {
                                    Question question = item.getQuestion();
                                    int questionOrder = item.getOrder() != null ? baseOrder + item.getOrder() - 1
                                            : baseOrder;

                                    FlattenedQuestionVM flattenedQuestion = new FlattenedQuestionVM();
                                    flattenedQuestion.setOrder(questionOrder);
                                    flattenedQuestion.setPart("");
                                    flattenedQuestion.setTranscript(question.getTranscript());
                                    flattenedQuestion.setShowTranscript(false);

                                    // Set answer based on question type
                                    if (question.getOptions() != null) {
                                        question.getOptions().stream()
                                                .filter(Option::isCorrect)
                                                .findFirst()
                                                .ifPresent(option -> flattenedQuestion.setAnswer(option.getText()));
                                    } else if (question.getFillBlankAnswers() != null
                                            && !question.getFillBlankAnswers().isEmpty()) {
                                        String answers = question.getFillBlankAnswers().stream()
                                                .map(FillBlankAnswer::getAnswerText)
                                                .collect(Collectors.joining(", "));
                                        flattenedQuestion.setAnswer(answers);
                                    }

                                    return flattenedQuestion;
                                });
                    })
                    .forEach(flattenedQuestions::add);
        }

        // Sort all items by their order
        flattenedQuestions.sort(Comparator.comparingInt(FlattenedQuestionVM::getOrder));
        vm.setFlattenedQuestions(flattenedQuestions);
        return vm;
    }
}