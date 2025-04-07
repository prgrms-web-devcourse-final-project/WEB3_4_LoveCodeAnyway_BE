package com.ddobang.backend.domain.upload.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum UploadErrorCode implements ErrorCode {
	// upload
	UPLOAD_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "UPLOAD_001", "해당 파일을 찾을 수 없습니다."),
	UPLOAD_FILE_MISSING_TARGET(HttpStatus.NOT_FOUND, "UPLOAD_002", "파일 업로드 대상이 필요합니다."),
	UPLOAD_FILE_INVALID_TARGET(HttpStatus.BAD_REQUEST, "UPLOAD_003", "파일 업로드 대상을 확인해주세요."),
	UPLOAD_FILE_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "UPLOAD_004", "허용하지 않는 확장자입니다."),
	UPLOAD_FILE_INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "UPLOAD_005", "파일 이름을 확인해주세요."),
	UPLOAD_FILE_FAIL_DELETE(HttpStatus.BAD_REQUEST, "UPLOAD_006", "파일 삭제에 실패했습니다.");

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
