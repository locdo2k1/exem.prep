package com.example.exam.prep.service;

import com.example.exam.prep.model.*;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.example.exam.prep.vm.test.TestCreateVM;
import com.example.exam.prep.vm.test.TestPartVM;
import com.example.exam.prep.vm.test.TestVM;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            
            // if (!nonEmptyFiles.isEmpty()) {
            //     // Upload files using the file storage service
            //     String uploadPath = "tests/" + savedTest.getId().toString();
            //     List<FileInfo> uploadedFiles = fileStorageService.uploadFiles(nonEmptyFiles, uploadPath);
                
            //     // Create TestFile relationships
            //     for (FileInfo fileInfo : uploadedFiles) {
            //         TestFile testFile = new TestFile(savedTest, fileInfo);
            //         unitOfWork.getTestFileRepository().save(testFile);
            //     }
            // }
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
        Part part = unitOfWork.getPartRepository().findById(partVM.getPartId())
            .orElseThrow(() -> new IllegalArgumentException("Part not found with id: " + partVM.getPartId()));
        
        // Create a new TestPart to link the test and part with order
        TestPart testPart = new TestPart(test, part, partVM.getOrder());
        
        return unitOfWork.getTestPartRepository().save(testPart);
    }

    private void handlePartQuestions(TestPartVM partVM, TestPart testPart) {
        // Handle question sets for this test part if any
        if (partVM.getListQuestionSet() != null && !partVM.getListQuestionSet().isEmpty()) {
            partVM.getListQuestionSet().forEach(questionSetOrder -> {
                QuestionSet questionSet = unitOfWork.getQuestionSetRepository()
                    .findById(questionSetOrder.getQuestionSetId())
                    .orElseThrow(() -> new IllegalArgumentException("Question set not found with id: " + questionSetOrder.getQuestionSetId()));
                
                testPart.getQuestionSets().add(questionSet);
            });
        }
        
        // Handle individual questions for this test part if any
        if (partVM.getListQuestion() != null && !partVM.getListQuestion().isEmpty()) {
            partVM.getListQuestion().forEach(questionOrder -> {
                Question question = unitOfWork.getQuestionRepository()
                    .findById(questionOrder.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionOrder.getQuestionId()));
                
                testPart.getQuestions().add(question);
            });
        }
        
        // Save the updated test part with its relationships
        unitOfWork.getTestPartRepository().save(testPart);
    }



    @Override
    public TestVM findById(UUID id) {
        Test test = unitOfWork.getTestRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + id));
        return TestVM.fromEntity(test);
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