package com.ddobang.backend.global.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
	private final ErrorCode errorCode;

	public ServiceException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ServiceException(ErrorCode errorCode, String customMessage) {
		super(customMessage);
		this.errorCode = errorCode;
	}
}
