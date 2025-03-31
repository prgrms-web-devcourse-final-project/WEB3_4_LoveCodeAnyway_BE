package com.ddobang.backend.domain.message.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum MessageErrorCode implements ErrorCode {

	MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_001", "쪽지를 찾을 수 없습니다."),
	MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MESSAGE_002", "쪽지 전송에 실패했습니다."),
	MESSAGE_READ_FORBIDDEN(HttpStatus.FORBIDDEN, "MESSAGE_003", "쪽지 읽음 상태 변경 권한이 없습니다."),
	MESSAGE_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "MESSAGE_004", "쪽지 삭제 권한이 없습니다."),
	MESSAGE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "MESSAGE_005", "쪽지 접근 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	MessageErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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