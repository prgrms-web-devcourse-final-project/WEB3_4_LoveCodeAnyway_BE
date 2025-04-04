package com.ddobang.backend.domain.upload.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum UploadErrorCode implements ErrorCode {
	// upload
	UPLOAD_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "UPLOAD_001", "해당 파일을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	UploadErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
