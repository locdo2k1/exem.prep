package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.TestPartAttempt;
import com.example.exam.prep.repository.ITestPartAttemptRepository;
import com.example.exam.prep.service.ITestPartAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestPartAttemptServiceImpl implements ITestPartAttemptService {

    private final ITestPartAttemptRepository testPartAttemptRepository;

    @Override
    @Transactional
    public TestPartAttempt startPracticeAttempt(UUID testPartId, UUID userId) {
        // Implementation will be added later
        return null;
    }

    @Override
    @Transactional
    public TestPartAttempt submitPracticeAttempt(UUID attemptId, UUID userId) {
        // Implementation will be added later
        return null;
    }

    @Override
    @Transactional
    public TestPartAttempt savePracticeProgress(UUID attemptId, UUID userId, int timeSpentSeconds) {
        // Implementation will be added later
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public TestPartAttempt getActivePracticeAttempt(UUID testPartId, UUID userId) {
        // Implementation will be added later
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestPartAttempt> getTestPartAttemptsByTestAttemptId(UUID testAttemptId) {
        return testPartAttemptRepository.findByTestAttemptId(testAttemptId);
    }
}
