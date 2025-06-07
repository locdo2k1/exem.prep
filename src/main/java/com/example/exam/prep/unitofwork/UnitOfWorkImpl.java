package com.example.exam.prep.unitofwork;

import com.example.exam.prep.repository.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnitOfWorkImpl implements IUnitOfWork {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IFillBlankAnswerRepository fillBlankAnswerRepository;
    @Autowired
    private IOptionRepository optionRepository;
    @Autowired
    private IPartRepository partRepository;
    @Autowired
    private IQuestionRepository questionRepository;
    @Autowired
    private IQuestionCategoryRepository questionCategoryRepository;
    @Autowired
    private IQuestionOptionRepository questionOptionRepository;
    @Autowired
    private IQuestionResponseRepository questionResponseRepository;
    @Autowired
    private IQuestionSetRepository questionSetRepository;
    @Autowired
    private IQuestionTypeRepository questionTypeRepository;
    @Autowired
    private ITestRepository testRepository;
    @Autowired
    private ITestAttemptRepository testAttemptRepository;
    @Autowired
    private ITestPartRepository testPartRepository;
    @Autowired
    private IFileInfoRepository fileInfoRepository;

    public UnitOfWorkImpl(EntityManager entityManager) {
    }

    @Override
    public IUserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    public IFillBlankAnswerRepository getFillBlankAnswerRepository() {
        return fillBlankAnswerRepository;
    }

    @Override
    public IOptionRepository getOptionRepository() {
        return optionRepository;
    }

    @Override
    public IPartRepository getPartRepository() {
        return partRepository;
    }

    @Override
    public IQuestionRepository getQuestionRepository() {
        return questionRepository;
    }

    @Override
    public IQuestionCategoryRepository getQuestionCategoryRepository() {
        return questionCategoryRepository;
    }

    @Override
    public IQuestionOptionRepository getQuestionOptionRepository() {
        return questionOptionRepository;
    }

    @Override
    public IQuestionResponseRepository getQuestionResponseRepository() {
        return questionResponseRepository;
    }

    @Override
    public IQuestionSetRepository getQuestionSetRepository() {
        return questionSetRepository;
    }

    @Override
    public IQuestionTypeRepository getQuestionTypeRepository() {
        return questionTypeRepository;
    }

    @Override
    public ITestRepository getTestRepository() {
        return testRepository;
    }

    @Override
    public ITestAttemptRepository getTestAttemptRepository() {
        return testAttemptRepository;
    }

    @Override
    public ITestPartRepository getTestPartRepository() {
        return testPartRepository;
    }

    @Override
    public IFileInfoRepository getFileInfoRepository() {
        return fileInfoRepository;
    }
}