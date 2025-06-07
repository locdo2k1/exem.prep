package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

public enum QuestionTypeResponseMessage implements BaseResponseMessage {
    QUESTION_TYPE_NOT_FOUND("Question type not found"),
    QUESTION_TYPE_RETRIEVED("Question type retrieved successfully"),
    QUESTION_TYPES_RETRIEVED("Question types retrieved successfully"),
    QUESTION_TYPE_CREATED("Question type created successfully"),
    QUESTION_TYPE_UPDATED("Question type updated successfully"),
    QUESTION_TYPE_DELETED("Question type deleted successfully"),
    QUESTION_TYPE_ALREADY_EXISTS("Question type with this code already exists");

    private final String message;

    QuestionTypeResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static String getNotFoundMessage() {
        return QUESTION_TYPE_NOT_FOUND.getMessage();
    }
}
