package com.example.exam.prep.service.impl;

import com.example.exam.prep.constant.response.TestCategoryResponseMessage;
import com.example.exam.prep.exception.ResourceNotFoundException;
import com.example.exam.prep.model.TestCategory;
import com.example.exam.prep.repository.TestCategoryRepository;
import com.example.exam.prep.service.ITestCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestCategoryServiceImpl implements ITestCategoryService {

    private final TestCategoryRepository testCategoryRepository;

    @Override
    public List<TestCategory> getAllTestCategories() {
        return testCategoryRepository.findAll();
    }

    @Override
    public TestCategory getTestCategoryById(Long id) {
        return testCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TestCategoryResponseMessage.TEST_CATEGORY_NOT_FOUND, id)));
    }

    @Override
    public TestCategory getTestCategoryByCode(String code) {
        return testCategoryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TestCategoryResponseMessage.TEST_CATEGORY_NOT_FOUND_BY_CODE, code)));
    }

    @Override
    @Transactional
    public TestCategory createTestCategory(TestCategory testCategory) {
        if (testCategoryRepository.existsByCode(testCategory.getCode())) {
            throw new IllegalArgumentException(String.format(TestCategoryResponseMessage.TEST_CATEGORY_CODE_EXISTS, testCategory.getCode()));
        }
        if (testCategoryRepository.existsByName(testCategory.getName())) {
            throw new IllegalArgumentException(String.format(TestCategoryResponseMessage.TEST_CATEGORY_NAME_EXISTS, testCategory.getName()));
        }
        return testCategoryRepository.save(testCategory);
    }

    @Override
    @Transactional
    public TestCategory updateTestCategory(Long id, TestCategory testCategoryDetails) {
        TestCategory testCategory = getTestCategoryById(id);
        
        // Check if code is being changed and if the new code already exists
        if (!testCategory.getCode().equals(testCategoryDetails.getCode()) && 
            testCategoryRepository.existsByCode(testCategoryDetails.getCode())) {
            throw new IllegalArgumentException(String.format(TestCategoryResponseMessage.TEST_CATEGORY_CODE_EXISTS, testCategoryDetails.getCode()));
        }
        
        // Check if name is being changed and if the new name already exists
        if (!testCategory.getName().equals(testCategoryDetails.getName()) && 
            testCategoryRepository.existsByName(testCategoryDetails.getName())) {
            throw new IllegalArgumentException(String.format(TestCategoryResponseMessage.TEST_CATEGORY_NAME_EXISTS, testCategoryDetails.getName()));
        }

        testCategory.setCode(testCategoryDetails.getCode());
        testCategory.setName(testCategoryDetails.getName());
        
        return testCategoryRepository.save(testCategory);
    }

    @Override
    @Transactional
    public void deleteTestCategory(Long id) {
        TestCategory testCategory = getTestCategoryById(id);
        testCategoryRepository.delete(testCategory);
    }

    @Override
    public boolean existsByCode(String code) {
        return testCategoryRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        return testCategoryRepository.existsByName(name);
    }
}
