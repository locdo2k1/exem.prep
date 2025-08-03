package com.example.exam.prep.unitofwork;

import com.example.exam.prep.repository.*;
import com.example.exam.prep.repository.ITestCategoryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Centralized access to all repositories with lazy initialization.
 */
@Service
@Lazy
public class UnitOfWorkImpl implements IUnitOfWork {
    // User related
    @Lazy private final IUserRepository userRepository;
    @Lazy private final IFileInfoRepository fileInfoRepository;
    
    // Question related
    @Lazy private final IQuestionRepository questionRepository;
    @Lazy private final IQuestionCategoryRepository questionCategoryRepository;
    @Lazy private final IQuestionOptionRepository questionOptionRepository;
    @Lazy private final IQuestionResponseRepository questionResponseRepository;
    @Lazy private final IQuestionSetRepository questionSetRepository;
    @Lazy private final IQuestionSetItemRepository questionSetItemRepository;
    @Lazy private final IQuestionTypeRepository questionTypeRepository;
    
    // Test related
    @Lazy private final ITestRepository testRepository;
    @Lazy private final ITestAttemptRepository testAttemptRepository;
    @Lazy private final ITestPartRepository testPartRepository;
    @Lazy private final ITestCategoryRepository testCategoryRepository;
    @Lazy private final ITestFileRepository testFileRepository;
    @Lazy private final ITestQuestionDetailRepository testQuestionDetailRepository;
    @Lazy private final ITestQuestionSetDetailRepository testQuestionSetDetailRepository;
    @Lazy private final ITestPartAttemptRepository testPartAttemptRepository;
    
    // Other repositories
    @Lazy private final IFillBlankAnswerRepository fillBlankAnswerRepository;
    @Lazy private final IOptionRepository optionRepository;
    @Lazy private final IPartRepository partRepository;
    @Lazy private final ISkillRepository skillRepository;
    @Lazy private final ITestSkillRepository testSkillRepository;
    
    @Lazy private final EntityManager entityManager;

    public UnitOfWorkImpl(
        @Lazy IUserRepository userRepository,
        @Lazy IFileInfoRepository fileInfoRepository,
        @Lazy IQuestionRepository questionRepository,
        @Lazy IQuestionCategoryRepository questionCategoryRepository,
        @Lazy IQuestionOptionRepository questionOptionRepository,
        @Lazy IQuestionResponseRepository questionResponseRepository,
        @Lazy IQuestionSetRepository questionSetRepository,
        @Lazy IQuestionSetItemRepository questionSetItemRepository,
        @Lazy IQuestionTypeRepository questionTypeRepository,
        @Lazy ITestRepository testRepository,
        @Lazy ISkillRepository skillRepository,
        @Lazy ITestSkillRepository testSkillRepository,
        @Lazy ITestAttemptRepository testAttemptRepository,
        @Lazy ITestPartRepository testPartRepository,
        @Lazy ITestCategoryRepository testCategoryRepository,
        @Lazy ITestFileRepository testFileRepository,
        @Lazy ITestQuestionDetailRepository testQuestionDetailRepository,
        @Lazy ITestQuestionSetDetailRepository testQuestionSetDetailRepository,
        @Lazy ITestPartAttemptRepository testPartAttemptRepository,
        @Lazy IFillBlankAnswerRepository fillBlankAnswerRepository,
        @Lazy IOptionRepository optionRepository,
        @Lazy IPartRepository partRepository,
        @Lazy EntityManager entityManager
    ) {
        this.userRepository = userRepository;
        this.fileInfoRepository = fileInfoRepository;
        this.questionRepository = questionRepository;
        this.questionCategoryRepository = questionCategoryRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.questionResponseRepository = questionResponseRepository;
        this.questionSetRepository = questionSetRepository;
        this.questionSetItemRepository = questionSetItemRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.testPartRepository = testPartRepository;
        this.testCategoryRepository = testCategoryRepository;
        this.testFileRepository = testFileRepository;
        this.testQuestionDetailRepository = testQuestionDetailRepository;
        this.testQuestionSetDetailRepository = testQuestionSetDetailRepository;
        this.testPartAttemptRepository = testPartAttemptRepository;
        this.fillBlankAnswerRepository = fillBlankAnswerRepository;
        this.optionRepository = optionRepository;
        this.partRepository = partRepository;
        this.skillRepository = skillRepository;
        this.testSkillRepository = testSkillRepository;
        this.entityManager = entityManager;
    }

    @Override public IUserRepository getUserRepository() { return userRepository; }
    @Override public IFileInfoRepository getFileInfoRepository() { return fileInfoRepository; }
    @Override public IQuestionRepository getQuestionRepository() { return questionRepository; }
    @Override public IQuestionCategoryRepository getQuestionCategoryRepository() { return questionCategoryRepository; }
    @Override public IQuestionOptionRepository getQuestionOptionRepository() { return questionOptionRepository; }
    @Override public IQuestionResponseRepository getQuestionResponseRepository() { return questionResponseRepository; }
    @Override public IQuestionSetRepository getQuestionSetRepository() { return questionSetRepository; }
    @Override public IQuestionSetItemRepository getQuestionSetItemRepository() { return questionSetItemRepository; }
    @Override public IQuestionTypeRepository getQuestionTypeRepository() { return questionTypeRepository; }
    @Override public ITestRepository getTestRepository() { return testRepository; }
    @Override public ITestAttemptRepository getTestAttemptRepository() { return testAttemptRepository; }
    @Override public ITestPartRepository getTestPartRepository() { return testPartRepository; }
    @Override public IFillBlankAnswerRepository getFillBlankAnswerRepository() { return fillBlankAnswerRepository; }
    @Override public IOptionRepository getOptionRepository() { return optionRepository; }
    @Override public IPartRepository getPartRepository() { return partRepository; }
    @Override public ITestFileRepository getTestFileRepository() { return testFileRepository; }
    @Override public ISkillRepository getSkillRepository() { return skillRepository; }
    @Override public ITestSkillRepository getTestSkillRepository() { return testSkillRepository; }
    @Override public ITestCategoryRepository getTestCategoryRepository() { return testCategoryRepository; }
    @Override public ITestQuestionDetailRepository getTestQuestionDetailRepository() { return testQuestionDetailRepository; }
    @Override public ITestQuestionSetDetailRepository getTestQuestionSetDetailRepository() { return testQuestionSetDetailRepository; }
    @Override public ITestPartAttemptRepository getTestPartAttemptRepository() { return testPartAttemptRepository; }
}