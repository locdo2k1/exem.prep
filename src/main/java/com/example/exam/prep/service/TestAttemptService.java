package com.example.exam.prep.service;

import com.example.exam.prep.constant.status.TestStatus;
import com.example.exam.prep.exception.NotFoundException;
import com.example.exam.prep.model.Test;
import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.model.User;
import com.example.exam.prep.repository.ITestAttemptRepository;
import com.example.exam.prep.service.base.BaseService;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.example.exam.prep.vm.test.TestVM;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing test attempts.
 */
@Service
public class TestAttemptService extends BaseService<TestAttempt> implements ITestAttemptService {
    
    private final IUnitOfWork unitOfWork;
    private final IUserService userService;
    private final ITestService testService;
    
    public TestAttemptService(IUnitOfWork unitOfWork, 
                            IUserService userService, 
                            ITestService testService) {
        super(unitOfWork.getTestAttemptRepository());
        this.unitOfWork = unitOfWork;
        this.userService = userService;
        this.testService = testService;
    }
    
    private ITestAttemptRepository getRepository() {
        return unitOfWork.getTestAttemptRepository();
    }

    @Override
    @Transactional
    public TestAttempt startTestAttempt(UUID testId, UUID userId) {
        User user = Optional.ofNullable(userService.getUser(userId))
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        // Get test VM and convert to entity
        TestVM testVM = testService.findById(testId);
        Test test = new Test();
        test.setId(testVM.getId());
        
        // Check if there's an existing ongoing attempt
        getRepository().findByTestIdAndUserId(testId, userId).stream()
                .filter(attempt -> attempt.getStatus() == TestStatus.ONGOING)
                .findFirst()
                .ifPresent(attempt -> {
                    throw new IllegalStateException("There is already an ongoing attempt for this test");
                });
        
        TestAttempt attempt = new TestAttempt();
        attempt.setUser(user);
        attempt.setTest(test);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus(TestStatus.ONGOING);
        
        return getRepository().save(attempt);
    }
    
    @Override
    @Transactional
    public TestAttempt submitTestAttempt(UUID attemptId, UUID userId) {
        TestAttempt attempt = getValidAttempt(attemptId, userId);
        
        if (attempt.getStatus() != TestStatus.ONGOING) {
            throw new IllegalStateException("Cannot submit a test attempt that is not in progress");
        }
        
        attempt.setStatus(TestStatus.SUBMITTED);
        attempt.setEndTime(LocalDateTime.now());
        
        return getRepository().save(attempt);
    }
    
    @Override
    @Transactional
    public TestAttempt updateTestAttemptStatus(UUID attemptId, TestStatus status) {
        TestAttempt attempt = getTestAttemptById(attemptId);
        
        attempt.setStatus(status);
        if (status == TestStatus.SUBMITTED && attempt.getEndTime() == null) {
            attempt.setEndTime(LocalDateTime.now());
        }
        
        return getRepository().save(attempt);
    }
    
    @Override
    public TestAttempt getTestAttemptById(UUID attemptId) {
        return getRepository().findById(attemptId)
                .orElseThrow(() -> new NotFoundException("Test attempt not found with id: " + attemptId));
    }
    
    @Override
    @Transactional
    public TestAttempt calculateAndUpdateScore(UUID attemptId) {
        TestAttempt attempt = getTestAttemptById(attemptId);
        
        if (attempt.getStatus() != TestStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot calculate score for an unsubmitted test attempt");
        }
        
        // TODO: Implement actual score calculation based on test part attempts
        // For now, we'll just set a placeholder score
        attempt.setTotalScore(0.0);
        
        return getRepository().save(attempt);
    }
    
    @Override
    public TestStatus getTestAttemptStatus(UUID attemptId) {
        return getTestAttemptById(attemptId).getStatus();
    }
    
    private TestAttempt getValidAttempt(UUID attemptId, UUID userId) {
        TestAttempt attempt = getTestAttemptById(attemptId);
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to access this test attempt");
        }
        
        return attempt;
    }
}

