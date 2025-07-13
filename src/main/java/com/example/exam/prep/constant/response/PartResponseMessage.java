package com.example.exam.prep.constant.response;

import com.example.exam.prep.constant.response.base.BaseResponseMessage;

public enum PartResponseMessage implements BaseResponseMessage {
    PART_NOT_FOUND("Part not found"),
    PART_RETRIEVED("Part retrieved successfully"),
    PARTS_RETRIEVED("Parts retrieved successfully"),
    PART_CREATED("Part created successfully"),
    PART_UPDATED("Part updated successfully"),
    PART_DELETED("Part deleted successfully"),
    PART_ALREADY_EXISTS("Part with this name already exists");

    private final String message;

    PartResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static String getNotFoundMessage() {
        return PART_NOT_FOUND.getMessage();
    }
}
