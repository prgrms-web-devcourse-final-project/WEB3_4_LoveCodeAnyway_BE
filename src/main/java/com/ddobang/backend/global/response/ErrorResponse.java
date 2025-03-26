package com.ddobang.backend.global.response;

import com.ddobang.backend.global.exception.ErrorCode;

public record ErrorResponse(String errorCode, String message) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getErrorCode(), errorCode.getMessage());
    }

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }
}
