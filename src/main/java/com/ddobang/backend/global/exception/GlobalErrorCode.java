package com.ddobang.backend.global.exception;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {

	// Common
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_002", "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_003", "접근 권한이 없습니다."),
	NOT_VALID(HttpStatus.BAD_REQUEST, "COMMON_004", "입력값이 올바르지 않습니다."),
	OPTIMISTIC_LOCKING_FAILURE(HttpStatus.CONFLICT, "COMMON-005", "요청이 충돌되었습니다. 다시 시도해주세요."),

	// Server
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_500", "서버 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	GlobalErrorCode(HttpStatus httpStatus, String errorCode, String message) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
