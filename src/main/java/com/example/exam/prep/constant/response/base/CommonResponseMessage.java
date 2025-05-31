package com.example.exam.prep.constant.response.base;

public enum CommonResponseMessage implements BaseResponseMessage {
    NOT_FOUND("Resource not found"),
    RETRIEVED("Resource retrieved successfully"),
    CREATED("Resource created successfully"),
    UPDATED("Resource updated successfully"),
    DELETED("Resource deleted successfully"),
    LIST_RETRIEVED("Resources retrieved successfully");

    private final String message;

    CommonResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

