package com.ddobang.backend.domain.theme.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ThemeErrorCode implements ErrorCode {
    // Theme
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "THEME_001", "테마를 찾을 수 없습니다."),

    // Theme tag
    THEME_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "THEME_TAG_001", "테마 태그를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    ThemeErrorCode(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() { return httpStatus; }

    @Override
    public String getErrorCode() { return errorCode; }

    @Override
    public String getMessage() { return message; }
}
