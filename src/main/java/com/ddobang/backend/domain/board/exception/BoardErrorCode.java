package com.ddobang.backend.domain.board.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum BoardErrorCode implements ErrorCode {

	BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_001", "게시글을 찾을 수 없습니다."),
	BOARD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "BOARD_002", "게시글에 접근할 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	BoardErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
