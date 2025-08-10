package com.example.exam.prep.viewmodel;

import com.example.exam.prep.model.TestPartAttempt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * View model for TestPartAttempt entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestPartAttemptVM {
    private UUID id;
    private UUID partId;
    private UUID testAttemptId;

    /**
     * Creates a TestPartAttemptVM from a TestPartAttempt entity
     * @param entity The TestPartAttempt entity to convert
     * @return A new TestPartAttemptVM instance, or null if the input is null
     */
    public static TestPartAttemptVM fromEntity(TestPartAttempt entity) {
        if (entity == null) {
            return null;
        }
        
        TestPartAttemptVM vm = new TestPartAttemptVM();
        vm.setId(entity.getId());
        
        if (entity.getPart() != null) {
            vm.setPartId(entity.getPart().getId());
        }
        
        if (entity.getTestAttempt() != null) {
            vm.setTestAttemptId(entity.getTestAttempt().getId());
        }
        
        return vm;
    }
}
