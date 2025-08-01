package com.example.exam.prep.vm.test;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class TestVMSimple {
    private UUID id;
    private String name;
    private String category;
    private java.util.List<TestSkillVM> listSkill;

    public static TestVMSimple fromEntity(com.example.exam.prep.model.Test test) {
        if (test == null) return null;
        TestVMSimple vm = new TestVMSimple();
        vm.setId(test.getId());
        vm.setName(test.getName());
        vm.setCategory(test.getTestCategory() != null ? test.getTestCategory().getName() : null);
        if (test.getTestSkills() != null && !test.getTestSkills().isEmpty()) {
            vm.setListSkill(test.getTestSkills().stream().map(testSkill -> {
                com.example.exam.prep.model.Skill skill = testSkill.getSkill();
                return TestSkillVM.builder()
                        .code(skill != null ? skill.getCode() : null)
                        .name(skill != null ? skill.getName() : null)
                        .description(skill != null ? skill.getDescription() : null)
                        .build();
            }).collect(java.util.stream.Collectors.toList()));
        } else {
            vm.setListSkill(java.util.Collections.emptyList());
        }
        return vm;
    }
}
