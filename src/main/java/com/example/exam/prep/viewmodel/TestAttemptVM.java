package com.example.exam.prep.viewmodel;

import com.example.exam.prep.model.TestAttempt;
import com.example.exam.prep.constant.status.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * View model for TestAttempt entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAttemptVM {
    private UUID id;
    private UUID testId;
    private UUID userId;
    private Instant startTime;
    private Instant endTime;
    private Double totalScore;
    private TestStatus status;

    /**
     * Creates a TestAttemptVM from a TestAttempt entity
     * @param entity The TestAttempt entity to convert
     * @return A new TestAttemptVM instance, or null if the input is null
     */
    public static TestAttemptVM fromEntity(TestAttempt entity) {
        if (entity == null) {
            return null;
        }
        
        TestAttemptVM vm = new TestAttemptVM();
        vm.setId(entity.getId());
        
        if (entity.getTest() != null) {
            vm.setTestId(entity.getTest().getId());
        }
        
        if (entity.getUser() != null) {
            vm.setUserId(entity.getUser().getId());
        }
        
        vm.setStartTime(entity.getStartTime());
        vm.setEndTime(entity.getEndTime());
        vm.setTotalScore(entity.getTotalScore());
        vm.setStatus(entity.getStatus());
        
        return vm;
    }
}
