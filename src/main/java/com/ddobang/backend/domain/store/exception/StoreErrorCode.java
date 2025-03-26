package com.ddobang.backend.domain.store.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum StoreErrorCode implements ErrorCode {
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_001", "매장을 찾을 수 없습니다."),
    STORE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "STORE_002", "이미 존재하는 매장입니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    StoreErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
