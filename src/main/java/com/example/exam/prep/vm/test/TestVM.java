package com.example.exam.prep.vm.test;

import com.example.exam.prep.model.Test;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TestVM {
    private UUID id;
    private String title;
    private List<TestPartVM> listPart;
    private List<TestQuestionSetVM> listQuestionSet;
    private List<TestQuestionVM> listQuestion;
    private List<TestSkillVM> listSkill;   
    private TestCategoryVM testCategory;
    private boolean isActive;

    public static TestVM fromEntity(Test test) {
        if (test == null) {
            return null;
        }
        
        TestVM vm = new TestVM();
        vm.setId(test.getId());
        vm.setTitle(test.getName());
        
        if (test.getTestCategory() != null) {
            TestCategoryVM categoryVM = TestCategoryVM.builder()
                    .id(test.getTestCategory().getId())
                    .name(test.getTestCategory().getName())
                    .build();
            vm.setTestCategory(categoryVM);
        }
    
        return vm;
    }
}
