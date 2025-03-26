package com.ddobang.backend.global.response;

public record SuccessResponse<T>(String message, T data) {
    public static <T> SuccessResponse<T> of(String message, T data) {
        return new SuccessResponse<>(message, data);
    }

    public static <T> SuccessResponse<T> of(String message) {
        return new SuccessResponse<>(message, null);
    }
}
