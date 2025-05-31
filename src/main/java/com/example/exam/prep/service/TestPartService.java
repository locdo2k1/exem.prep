package com.example.exam.prep.service;

import com.example.exam.prep.model.TestPart;
import com.example.exam.prep.repository.ITestPartRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class TestPartService extends BaseService<TestPart> {
    private final ITestPartRepository testPartRepository;

    public TestPartService(ITestPartRepository testPartRepository) {
        super(testPartRepository);
        this.testPartRepository = testPartRepository;
    }
}
