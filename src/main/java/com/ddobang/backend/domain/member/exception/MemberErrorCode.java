package com.ddobang.backend.domain.member.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MemberErrorCode implements ErrorCode {
    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "멤버를 찾을 수 없습니다."),
    MEMBER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MEMBER_002", "멤버 인증에 실패했습니다."),

    // Member Tag
    MEMBER_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_TAG_001", "멤버 태그를 찾을 수 없습니다."),

    // Member Review
    MEMBER_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_001", "멤버 리뷰를 찾을 수 없습니다."),
    MEMBER_REVIEW_DUPLICATED(HttpStatus.BAD_REQUEST, "REVIEW_002", "이미 멤버 리뷰를 작성하셨습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    MemberErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
