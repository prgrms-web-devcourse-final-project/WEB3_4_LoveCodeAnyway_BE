package com.ddobang.backend.global.exception.auth;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	// signup
	INVALID_SIGNUP_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 회원가입 토큰입니다."),
	SIGNUP_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_002", "회원가입 토큰이 만료되었습니다."),
	ALREADY_REGISTERED(HttpStatus.CONFLICT, "AUTH_003", "이미 등록된 회원입니다.");

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
