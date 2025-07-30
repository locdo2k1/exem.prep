package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.TestCategoryResponseMessage;
import com.example.exam.prep.model.TestCategory;
import com.example.exam.prep.service.ITestCategoryService;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestCategoryController {

    private final ITestCategoryService testCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TestCategory>>> getAllTestCategories() {
        List<TestCategory> categories = testCategoryService.getAllTestCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, TestCategoryResponseMessage.TEST_CATEGORIES_RETRIEVED));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCategory>> getTestCategoryById(@PathVariable Long id) {
        TestCategory category = testCategoryService.getTestCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category, TestCategoryResponseMessage.TEST_CATEGORY_RETRIEVED));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<TestCategory>> getTestCategoryByCode(@PathVariable String code) {
        TestCategory category = testCategoryService.getTestCategoryByCode(code);
        return ResponseEntity.ok(ApiResponse.success(category, TestCategoryResponseMessage.TEST_CATEGORY_RETRIEVED));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TestCategory>> createTestCategory(@RequestBody TestCategory testCategory) {
        TestCategory createdCategory = testCategoryService.createTestCategory(testCategory);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCategory, TestCategoryResponseMessage.TEST_CATEGORY_CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCategory>> updateTestCategory(
            @PathVariable Long id, 
            @RequestBody TestCategory testCategory) {
        TestCategory updatedCategory = testCategoryService.updateTestCategory(id, testCategory);
        return ResponseEntity.ok(ApiResponse.success(updatedCategory, TestCategoryResponseMessage.TEST_CATEGORY_UPDATED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestCategory(@PathVariable Long id) {
        testCategoryService.deleteTestCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, TestCategoryResponseMessage.TEST_CATEGORY_DELETED));
    }

    @GetMapping("/exists/code/{code}")
    public ResponseEntity<ApiResponse<Boolean>> existsByCode(@PathVariable String code) {
        boolean exists = testCategoryService.existsByCode(code);
        return ResponseEntity.ok(ApiResponse.success(exists, TestCategoryResponseMessage.CHECK_CODE_EXISTENCE_COMPLETED));
    }

    @GetMapping("/exists/name/{name}")
    public ResponseEntity<ApiResponse<Boolean>> existsByName(@PathVariable String name) {
        boolean exists = testCategoryService.existsByName(name);
        return ResponseEntity.ok(ApiResponse.success(exists, TestCategoryResponseMessage.CHECK_NAME_EXISTENCE_COMPLETED));
    }
}
