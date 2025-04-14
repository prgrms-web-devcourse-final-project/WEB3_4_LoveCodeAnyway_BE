package com.ddobang.backend.domain.region.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

/**
 * RegionErrorCode
 * 지역 도메인 커스텀 ErrorCode
 * @author 100minha
 */
public enum RegionErrorCode implements ErrorCode {
	REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "REGION_001", "지역 정보를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	RegionErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
