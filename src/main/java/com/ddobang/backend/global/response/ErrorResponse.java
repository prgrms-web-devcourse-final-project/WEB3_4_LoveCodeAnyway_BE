package com.ddobang.backend.global.response;

import com.ddobang.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final String code;
    private final String message;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }
}
