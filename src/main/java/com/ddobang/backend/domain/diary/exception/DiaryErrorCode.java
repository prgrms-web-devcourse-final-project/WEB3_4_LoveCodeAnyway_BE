package com.ddobang.backend.domain.diary.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum DiaryErrorCode implements ErrorCode {
	// Diary
	DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY_001", "탈출일지를 찾을 수 없습니다."),
	DIARY_INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "DIARY_002", "잘못 된 시간 형식입니다."),
	DIARY_INVALID_TIME_TYPE(HttpStatus.BAD_REQUEST, "DIARY_003", "진행 시간인지, 남은 시간인지 확인해주세요."),
	DIARY_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "DIARY_004", "시작 날짜는 종료 날짜 이전이어야 합니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	DiaryErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
