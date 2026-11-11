package edu.itc.shop.api.dto;

import java.util.List;
import java.util.Map;

public record Problem(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        List<Map<String, String>> errors
) {
    public static final String MEDIA_TYPE = "application/problem+json";

    public Problem(String type, String title, int status, String detail, String instance) {
        this(type, title, status, detail, instance, null);
    }
}
