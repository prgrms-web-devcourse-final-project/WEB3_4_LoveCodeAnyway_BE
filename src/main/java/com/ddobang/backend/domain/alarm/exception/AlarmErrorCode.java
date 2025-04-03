package com.ddobang.backend.domain.alarm.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum AlarmErrorCode implements ErrorCode {

	ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI_001", "알림을 찾을 수 없습니다."),
	UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "NOTI_002", "알림에 접근 권한이 없습니다."),
	SSE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NOTI_003", "SSE 연결 중 오류가 발생했습니다."),
	SSE_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NOTI_004", "알림 전송 중 오류가 발생했습니다."),
	SSE_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "NOTI_005", "SSE 연결 시간이 초과되었습니다.");

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
