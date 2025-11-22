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
    private List<TestPartDetailVM> listPart;
    private List<TestQuestionItemVM> listQuestionItem;
    private List<TestSkillVM> listSkill;
    private TestCategoryVM testCategory;
    private boolean isActive;

    private Integer durationMinutes;
    private List<FileInfoVM> files;

    public static TestVM fromEntity(Test test) {
        if (test == null) {
            return null;
        }

        TestVM vm = new TestVM();
        vm.setId(test.getId());
        vm.setTitle(test.getName());
        vm.setDurationMinutes(test.getDurationMinutes());

        if (test.getTestCategory() != null) {
            TestCategoryVM categoryVM = TestCategoryVM.builder()
                    .id(test.getTestCategory().getId())
                    .name(test.getTestCategory().getName())
                    .build();
            vm.setTestCategory(categoryVM);
        }

        // Map test files to FileInfoVM
        if (test.getTestFiles() != null && !test.getTestFiles().isEmpty()) {
            vm.setFiles(test.getTestFiles().stream()
                    .map(testFile -> FileInfoVM.fromEntity(testFile.getFile()))
                    .filter(fileVM -> fileVM != null)
                    .collect(java.util.stream.Collectors.toList()));
        }

        return vm;
    }
}
