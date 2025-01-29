package com.example.CapstoneProject.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class APIResponse {
    private int statusCode;
    private String message;
    private Object data;

    public static APIResponse success(int statusCode, String message, Object data) {
        return new APIResponse(statusCode, message, data);
    }

    public static APIResponse error(int statusCode, String message) {
        return new APIResponse(statusCode, message, null);
    }
}
