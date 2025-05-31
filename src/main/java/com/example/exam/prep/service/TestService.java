package com.example.exam.prep.service;

import com.example.exam.prep.model.Test;
import com.example.exam.prep.repository.ITestRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class TestService extends BaseService<Test> {
    private final ITestRepository testRepository;

    public TestService(ITestRepository testRepository) {
        super(testRepository);
        this.testRepository = testRepository;
    }
}

