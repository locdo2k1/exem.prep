package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

public enum QuestionSetResponseMessage implements BaseResponseMessage {
    QUESTION_SET_NOT_FOUND("Question set not found"),
    QUESTION_SET_RETRIEVED("Question set retrieved successfully"),
    QUESTION_SETS_RETRIEVED("Question sets retrieved successfully"),
    QUESTION_SET_CREATED("Question set created successfully"),
    QUESTION_SET_UPDATED("Question set updated successfully"),
    QUESTION_SET_DELETED("Question set deleted successfully"),
    ID_MISMATCH("ID in path and request body do not match");

    private final String message;

    QuestionSetResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
