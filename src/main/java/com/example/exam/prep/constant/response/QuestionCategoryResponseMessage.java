package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

public enum QuestionCategoryResponseMessage implements BaseResponseMessage {
    QUESTION_CATEGORY_NOT_FOUND("Question category not found with id: %s"),
    QUESTION_CATEGORY_CREATED("Question category created successfully"),
    QUESTION_CATEGORY_UPDATED("Question category updated successfully"),
    QUESTION_CATEGORY_DELETED("Question category deleted successfully"),
    QUESTION_CATEGORIES_RETRIEVED("Question categories retrieved successfully"),
    QUESTION_CATEGORY_RETRIEVED("Question category retrieved successfully");

    private final String message;

    QuestionCategoryResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String formatMessage(Object... args) {
        return String.format(getMessage(), args);
    }
}
