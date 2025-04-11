package com.ddobang.backend.global.exception.jwt;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT_001", "토큰이 만료되었습니다."),
	TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "JWT_002", "유효하지 않은 토큰입니다."),
	TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "JWT_003", "토큰이 존재하지 않습니다."),
	UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_004", "지원하지 않는 토큰 형식입니다."),
	INVALID_PRINCIPAL(HttpStatus.UNAUTHORIZED, "JWT_005", "인증 정보가 올바르지 않습니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

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
