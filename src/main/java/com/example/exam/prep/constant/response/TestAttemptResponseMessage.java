package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

/**
 * Contains response messages for test attempt operations.
 */
public enum TestAttemptResponseMessage implements BaseResponseMessage {
    ATTEMPT_STARTED("Test attempt started successfully"),
    ATTEMPT_SUBMITTED("Test attempt submitted successfully"),
    ATTEMPT_RETRIEVED("Test attempt retrieved successfully"),
    ATTEMPTS_RETRIEVED("Test attempts retrieved successfully"),
    STATUS_RETRIEVED("Test attempt status retrieved successfully"),
    STATUS_UPDATED("Test attempt status updated successfully"),
    SCORE_CALCULATED("Test attempt score calculated successfully");

    private final String message;

    TestAttemptResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
