package com.example.exam.prep.constant.response;

public class TestCategoryResponseMessage {
    // Success messages
    public static final String TEST_CATEGORY_RETRIEVED = "Test category retrieved successfully";
    public static final String TEST_CATEGORIES_RETRIEVED = "Test categories retrieved successfully";
    public static final String TEST_CATEGORY_CREATED = "Test category created successfully";
    public static final String TEST_CATEGORY_UPDATED = "Test category updated successfully";
    public static final String TEST_CATEGORY_DELETED = "Test category deleted successfully";
    public static final String CHECK_CODE_EXISTENCE_COMPLETED = "Check code existence completed";
    public static final String CHECK_NAME_EXISTENCE_COMPLETED = "Check name existence completed";
    
    // Error messages
    public static final String TEST_CATEGORY_NOT_FOUND = "TestCategory not found with id: %s";
    public static final String TEST_CATEGORY_NOT_FOUND_BY_CODE = "TestCategory not found with code: %s";
    public static final String TEST_CATEGORY_CODE_EXISTS = "TestCategory with code %s already exists";
    public static final String TEST_CATEGORY_NAME_EXISTS = "TestCategory with name %s already exists";
}
