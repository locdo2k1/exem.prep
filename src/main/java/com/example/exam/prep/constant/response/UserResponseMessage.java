package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

public enum UserResponseMessage implements BaseResponseMessage {
    USERS_RETRIEVED("Users retrieved successfully"),
    USER_RETRIEVED("User retrieved successfully"),
    USER_CREATED("User created successfully"),
    USER_UPDATED("User updated successfully"),
    USER_DELETED("User deleted successfully"),
    USER_NOT_FOUND("User not found"),
    USER_CREATE_FAILED("Failed to create user"),
    USER_UPDATE_FAILED("Failed to update user"),
    USER_DELETE_FAILED("Failed to delete user");

    private final String message;

    UserResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static String getNotFoundMessage() {
        return USER_NOT_FOUND.getMessage();
    }
}
