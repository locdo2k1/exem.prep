package com.example.exam.prep.vm.test;

import com.example.exam.prep.model.Test;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestVM {
    private UUID id;
    private String name;
    private String description;
    private UUID testCategoryId;
    private String testCategoryName;
    private boolean isActive;

    public static TestVM fromEntity(Test test) {
        if (test == null) {
            return null;
        }
        
        TestVM vm = new TestVM();
        vm.setId(test.getId());
        vm.setName(test.getName());
        vm.setDescription(test.getDescription());
        
        if (test.getTestCategory() != null) {
            vm.setTestCategoryId(test.getTestCategory().getId());
            vm.setTestCategoryName(test.getTestCategory().getName());
        }
        
        return vm;
    }
}
