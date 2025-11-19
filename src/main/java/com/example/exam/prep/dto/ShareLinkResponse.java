package com.example.exam.prep.dto;

import lombok.Data;

@Data
public class ShareLinkResponse {
    private String url;
    private String id;
    private String name;
    private String path_lower;
    private String link_permissions;
    private String preview_type;
    private String client_modified;
    private String server_modified;
    private String rev;
    private long size;
    private String content_hash;
}
