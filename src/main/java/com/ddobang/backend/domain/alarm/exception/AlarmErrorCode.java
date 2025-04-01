package com.ddobang.backend.domain.alarm.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum AlarmErrorCode implements ErrorCode {

	ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI_001", "알림을 찾을 수 없습니다."),
	UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "NOTI_002", "알림에 접근 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	AlarmErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
