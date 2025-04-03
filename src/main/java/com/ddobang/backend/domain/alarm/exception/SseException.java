package com.ddobang.backend.domain.alarm.exception;

import java.io.IOException;

public class SseException extends AlarmException {

	private final IOException cause;

	public SseException(AlarmErrorCode errorCode, IOException cause) {
		super(errorCode);
		this.cause = cause;
	}

	public SseException(AlarmErrorCode errorCode) {
		super(errorCode);
		this.cause = null;
	}

	public IOException getCauseIoException() {
		return this.cause;
	}
}