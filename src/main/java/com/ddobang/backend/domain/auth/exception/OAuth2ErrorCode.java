package com.ddobang.backend.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2ErrorCode implements ErrorCode {
	OAUTH2_MISSING_ID(HttpStatus.UNAUTHORIZED, "OAUTH2_001", "OAuth2 사용자 정보에 ID가 없습니다."),
	OAUTH2_MISSING_NICKNAME(HttpStatus.UNAUTHORIZED, "OAUTH2_002", "카카오 사용자 정보에 닉네임이 없습니다."),
	OAUTH2_MEMBER_PROCESS_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH2_003", "회원 정보 처리 중 오류가 발생했습니다."),
	OAUTH2_TOKEN_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH2_004", "JWT 토큰 생성 중 오류가 발생했습니다.");

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
