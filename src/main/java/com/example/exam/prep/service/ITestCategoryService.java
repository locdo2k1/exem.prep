package com.example.exam.prep.service;

import com.example.exam.prep.model.TestCategory;
import java.util.List;

public interface ITestCategoryService {
    List<TestCategory> getAllTestCategories();
    TestCategory getTestCategoryById(Long id);
    TestCategory getTestCategoryByCode(String code);
    TestCategory createTestCategory(TestCategory testCategory);
    TestCategory updateTestCategory(Long id, TestCategory testCategory);
    void deleteTestCategory(Long id);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
