package com.ddobang.backend.domain.diary.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum DiaryErrorCode implements ErrorCode {
    // Diary
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_001", "다이어리를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    DiaryErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
