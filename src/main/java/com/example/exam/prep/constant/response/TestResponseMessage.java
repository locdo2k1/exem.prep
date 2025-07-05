package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TestResponseMessage implements BaseResponseMessage {
    TEST_CREATED("Test created successfully"),
    TEST_UPDATED("Test updated successfully"),
    TEST_DELETED("Test deleted successfully"),
    TEST_RETRIEVED("Test retrieved successfully"),
    TESTS_RETRIEVED("Tests retrieved successfully"),
    TEST_NOT_FOUND("Test not found");

    @Getter
    private final String message;
}
