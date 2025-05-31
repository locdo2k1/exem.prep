package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

public enum AuthResponseMessage implements BaseResponseMessage {
    LOGIN_SUCCESS("Login successful"),
    INVALID_CREDENTIALS("Invalid credentials"),
    MISSING_AUTH_HEADER("Missing or invalid Authorization header"),
    USERNAME_EXISTS("Username '%s' already exists"),
    USER_REGISTER_SUCCESS("User registered successfully"),
    FAILED_CREATE_USER("Failed to create user"),
    ERROR_CREATING_USER("Error creating user: %s"),
    TOKEN_GENERATED("Token generated successfully"),
    ERROR_GENERATING_TOKEN("Error generating auth token: %s");

    private final String message;

    AuthResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static String getErrorMessage(String errorDetails) {
        return String.format(ERROR_CREATING_USER.getMessage(), errorDetails);
    }

    public static String getUserExistsMessage(String username) {
        return String.format(USERNAME_EXISTS.getMessage(), username);
    }

    public static String getTokenErrorMessage(String errorDetails) {
        return String.format(ERROR_GENERATING_TOKEN.getMessage(), errorDetails);
    }
}
