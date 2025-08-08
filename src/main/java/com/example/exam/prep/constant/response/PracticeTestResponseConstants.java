package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

/**
 * Constants for Practice Test related responses.
 */
public enum PracticeTestResponseConstants implements BaseResponseMessage {
    /**
     * Success message when practice test data is retrieved successfully.
     */
    PRACTICE_TEST_RETRIEVED_SUCCESSFULLY("Practice test data retrieved successfully");
    
    private final String message;

    PracticeTestResponseConstants(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
