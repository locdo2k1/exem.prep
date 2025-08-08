package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

public enum QuestionResponseMessage implements BaseResponseMessage {
    QUESTION_NOT_FOUND("Question not found"),
    QUESTION_RETRIEVED("Question retrieved successfully"),
    QUESTIONS_RETRIEVED("Questions retrieved successfully"),
    QUESTION_CREATED("Question created successfully"),
    QUESTION_UPDATED("Question updated successfully"),
    QUESTION_DELETED("Question deleted successfully"),
    QUESTION_CREATE_ERROR("An error occurred while creating the question");

    private final String message;

    QuestionResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static String getNotFoundMessage() {
        return QUESTION_NOT_FOUND.getMessage();
    }
}

