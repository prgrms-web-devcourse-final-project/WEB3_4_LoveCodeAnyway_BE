package com.ddobang.backend.global.exception.oauth2;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2ErrorCode implements ErrorCode {
	// OAuth2 관련 에러 코드
	OAUTH2_MISSING_ID(HttpStatus.BAD_REQUEST, "OAUTH2_001", "OAuth2 사용자 정보에 ID가 없습니다."),
	OAUTH2_MEMBER_PROCESS_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH2_002", "OAuth2 사용자 정보 처리 중 오류가 발생했습니다."),
	OAUTH2_TOKEN_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH2_003", "OAuth2 JWT 토큰 생성에 실패했습니다."),
	OAUTH2_MISSING_STATE(HttpStatus.BAD_REQUEST, "OAUTH2_004", "OAuth2 로그인 요청에 state 파라미터가 없습니다.");

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
