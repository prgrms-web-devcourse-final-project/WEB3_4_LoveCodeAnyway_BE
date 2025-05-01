package com.ddobang.backend.domain.themewish.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

/**
 * ThemeWishErrorCode
 * <p></p>
 * @author 100minha
 */
public enum ThemeWishErrorCode implements ErrorCode {
	EXCEEDED_MAXIMUM_WISHES(HttpStatus.BAD_REQUEST, "THEME_WISH_001", "모임 희망 테마 수가 최대치를 초과했습니다."),
	ALREADY_WISHED(HttpStatus.BAD_REQUEST, "THEME_WISH_002", "이미 모임 희망중인 테마입니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	ThemeWishErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
