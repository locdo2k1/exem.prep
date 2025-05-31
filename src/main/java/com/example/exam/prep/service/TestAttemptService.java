package com.example.exam.prep.service;

import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.repository.ITestAttemptRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class TestAttemptService extends BaseService<TestAttempt> {
    private final ITestAttemptRepository testAttemptRepository;

    public TestAttemptService(ITestAttemptRepository testAttemptRepository) {
        super(testAttemptRepository);
        this.testAttemptRepository = testAttemptRepository;
    }
}

