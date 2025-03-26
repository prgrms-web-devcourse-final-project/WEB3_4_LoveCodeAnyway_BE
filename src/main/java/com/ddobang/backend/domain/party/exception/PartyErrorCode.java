package com.ddobang.backend.domain.party.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PartyErrorCode implements ErrorCode {
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTY_001", "모임을 찾을 수 없습니다."),
    PARTY_FULL(HttpStatus.BAD_REQUEST, "PARTY_002", "모임 인원이 가득 찼습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    PartyErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
