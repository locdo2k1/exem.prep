package com.example.exam.prep.dto;

import lombok.Data;

@Data
public class ShareLinkRequest {
    private String path;
    private ShareLinkSettings settings;

    @Data
    public static class ShareLinkSettings {
        private String access;
        private boolean allow_download;
        private String audience;
        private String requested_visibility;
    }
}
