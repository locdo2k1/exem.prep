package com.example.exam.prep.service;

import com.example.exam.prep.model.*;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.example.exam.prep.model.viewmodels.question.QuestionTypeViewModel;
import com.example.exam.prep.model.viewmodels.file.FileInfoViewModel;
import com.example.exam.prep.vm.test.TestCreateVM;
import com.example.exam.prep.vm.test.TestPartVM;
import com.example.exam.prep.vm.test.TestVM;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import com.example.exam.prep.model.viewmodels.option.OptionViewModel;
import com.example.exam.prep.model.viewmodels.question.QuestionCategoryViewModel;
import com.example.exam.prep.vm.test.*;
import com.example.exam.prep.model.*;

@Service
@Transactional
public class TestServiceImpl implements ITestService {
    private final IUnitOfWork unitOfWork;
    private final IFileStorageService fileStorageService;

    public TestServiceImpl(IUnitOfWork unitOfWork, IFileStorageService fileStorageService) {
        this.unitOfWork = unitOfWork;
        this.fileStorageService = fileStorageService;
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
                .orElseThrow(() -> new IllegalArgumentException("Test category not found with id: " + testVM.getTestCategoryId()));
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
        
        return unitOfWork.getTestPartRepository().save(testPart);
    }

    private void handlePartQuestions(TestPartVM partVM, TestPart testPart) {
        // Handle question sets for this test part if any
        if (partVM.getQuestionSets() != null && !partVM.getQuestionSets().isEmpty()) {
            partVM.getQuestionSets().forEach(questionSet -> {
                QuestionSet questionSetEntity = unitOfWork.getQuestionSetRepository()
                    .findById(questionSet.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Question set not found with id: " + questionSet.getId()));
                
                testPart.getQuestionSets().add(questionSetEntity);
            });
        }
        
        // Handle individual questions for this test part if any
        if (partVM.getQuestions() != null && !partVM.getQuestions().isEmpty()) {
            partVM.getQuestions().forEach(question -> {
                Question questionEntity = unitOfWork.getQuestionRepository()
                    .findById(question.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + question.getId()));
                
                testPart.getQuestions().add(questionEntity);
            });
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
        List<TestQuestionVM> allQuestions = new ArrayList<>();
        
        // Fetch and set test parts
        if (test.getTestParts() != null && !test.getTestParts().isEmpty()) {
            List<TestPartVM> partVMs = test.getTestParts().stream()
                .sorted((p1, p2) -> Integer.compare(p1.getOrderIndex(), p2.getOrderIndex()))
                .map(part -> {
                    TestPartVM partVM = new TestPartVM();
                    partVM.setId(part.getId());
                    partVM.setTitle(part.getPart() != null ? part.getPart().getName() : "");
                    partVM.setDescription(part.getPart() != null ? part.getPart().getDescription() : "");
                    partVM.setOrder(part.getOrderIndex());
                    
                    // Map questions in this part
                    if (part.getQuestions() != null) {
                        List<TestQuestionVM> questionVMs = part.getQuestions().stream()
                            .map(question -> {
                                TestQuestionVM questionVM = new TestQuestionVM();
                                questionVM.setId(question.getId());
                                questionVM.setPrompt(question.getPrompt());
                                questionVM.setPartId(part.getId()); // Set the part ID for reference
                                questionVM.setQuestionCategory(convertToQuestionCategoryViewModel(question.getCategory()));
                                questionVM.setQuestionType(convertToQuestionTypeViewModel(question.getQuestionType()));
                                questionVM.setScore(question.getScore());
                                // Map fillBlankAnswers to questionAnswers if they exist, otherwise use empty list
                                List<String> answers = question.getFillBlankAnswers() != null 
                                    ? question.getFillBlankAnswers().stream()
                                        .map(FillBlankAnswer::getAnswerText)
                                        .collect(Collectors.toList())
                                    : Collections.emptyList();
                                questionVM.setQuestionAnswers(answers);
                                // Convert Set<Option> to List<OptionViewModel>
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
                                
                                // Map FileInfo to FileInfoViewModel
                                List<FileInfoViewModel> fileInfoViewModels = question.getFileInfos() != null ?
                                    question.getFileInfos().stream()
                                        .map(file -> {
                                            FileInfoViewModel fileViewModel = new FileInfoViewModel();
                                            fileViewModel.setId(file.getId());
                                            fileViewModel.setFileName(file.getFileName());
                                            fileViewModel.setFileUrl(file.getUrl());
                                            fileViewModel.setFileType(file.getFileType());
                                            fileViewModel.setFileSize(file.getFileSize());
                                            return fileViewModel;
                                        })
                                        .collect(Collectors.toList()) :
                                    Collections.emptyList();
                                questionVM.setQuestionAudios(fileInfoViewModels);
                                
                                questionVM.setOrder(question.getOrder());
                                return questionVM;
                            })
                            .collect(Collectors.toList());
                        partVM.setQuestions(questionVMs);
                        allQuestions.addAll(questionVMs); // Add to master list
                    }
                    
                    // Map question sets in this part
                    if (part.getQuestionSets() != null) {
                        List<TestQuestionSetVM> questionSetVMs = part.getQuestionSets().stream()
                            .map(questionSet -> {
                                TestQuestionSetVM setVM = new TestQuestionSetVM();
                                setVM.setId(questionSet.getId());
                                setVM.setTitle(questionSet.getTitle());
                                setVM.setDescription(questionSet.getDescription());
                                setVM.setPartId(part.getId()); // Set the part ID for reference
                                setVM.setOrder(questionSet.getOrder());
                                setVM.setImageUrl(questionSet.getImageUrl());
                                
                                // Map questions in this question set
                                if (questionSet.getQuestionSetItems() != null) {
                                    List<TestQuestionVM> questionVMs = questionSet.getQuestionSetItems().stream()
                                        .filter(QuestionSetItem::getIsActive)
                                        .map(item -> {
                                            Question question = item.getQuestion();
                                            TestQuestionVM questionVM = new TestQuestionVM();
                                            questionVM.setId(question.getId());
                                            questionVM.setPrompt(question.getPrompt());
                                            questionVM.setQuestionType(convertToQuestionTypeViewModel(question.getQuestionType()));
                                            questionVM.setScore(item.getCustomScore() != null ? item.getCustomScore() : question.getScore());
                                            questionVM.setOrder(item.getOrder() != null ? item.getOrder() : 0);
                                            
                                            // Map fill blank answers if any
                                            if (question.getFillBlankAnswers() != null && !question.getFillBlankAnswers().isEmpty()) {
                                                List<String> answers = question.getFillBlankAnswers().stream()
                                                    .map(FillBlankAnswer::getAnswerText)
                                                    .collect(Collectors.toList());
                                                questionVM.setQuestionAnswers(answers);
                                            }
                                            
                                            // Map options if it's a multiple choice question
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
                                            if (question.getFileInfos() != null && !question.getFileInfos().isEmpty()) {
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
                                
                                return setVM;
                            })
                            .collect(Collectors.toList());
                        partVM.setQuestionSets(questionSetVMs);
                        allQuestionSets.addAll(questionSetVMs); // Add to master list
                    }
                    
                    return partVM;
                })
                .collect(Collectors.toList());
            vm.setListPart(partVMs);
        }
        
        // Map individual questions from testQuestionDetails
        if (test.getTestQuestionDetails() != null) {
            List<TestQuestionVM> individualQuestions = test.getTestQuestionDetails().stream()
                .sorted(Comparator.comparing(TestQuestionDetail::getOrder))
                .map(detail -> {
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
                    
                    return questionVM;
                })
                .collect(Collectors.toList());
            vm.setListQuestion(individualQuestions);
        }
        
        // Map question sets from testQuestionSetDetails
        if (test.getTestQuestionSetDetails() != null) {
            List<TestQuestionSetVM> questionSetVMs = test.getTestQuestionSetDetails().stream()
                .sorted(Comparator.comparing(TestQuestionSetDetail::getOrder))
                .map(detail -> {
                    QuestionSet questionSet = detail.getQuestionSet();
                    TestQuestionSetVM setVM = new TestQuestionSetVM();
                    setVM.setId(questionSet.getId());
                    setVM.setTitle(questionSet.getTitle());
                    setVM.setDescription(questionSet.getDescription());
                    setVM.setOrder(questionSet.getOrder());
                    setVM.setImageUrl(questionSet.getImageUrl());
                    
                    // Map questions in this question set
                    if (questionSet.getQuestionSetItems() != null) {
                        List<TestQuestionVM> questionVMs = questionSet.getQuestionSetItems().stream()
                            .filter(QuestionSetItem::getIsActive)
                            .sorted(Comparator.comparing(QuestionSetItem::getOrder, 
                                Comparator.nullsLast(Comparator.naturalOrder())))
                            .map(item -> {
                                Question question = item.getQuestion();
                                TestQuestionVM questionVM = new TestQuestionVM();
                                questionVM.setId(question.getId());
                                questionVM.setPrompt(question.getPrompt());
                                questionVM.setQuestionType(convertToQuestionTypeViewModel(question.getQuestionType()));
                                questionVM.setScore(item.getCustomScore() != null ? item.getCustomScore() : question.getScore());
                                questionVM.setOrder(item.getOrder() != null ? item.getOrder() : 0);
                                
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
                    
                    return setVM;
                })
                .collect(Collectors.toList());
            vm.setListQuestionSet(questionSetVMs);
        }
        
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
                    .orElseThrow(() -> new IllegalArgumentException("Question set not found with id: " + questionSetOrder.getQuestionSetId()));
                
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
                    .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionOrder.getQuestionId()));
                
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
}