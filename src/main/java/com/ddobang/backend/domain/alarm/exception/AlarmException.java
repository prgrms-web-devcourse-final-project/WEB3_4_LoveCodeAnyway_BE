package com.ddobang.backend.domain.alarm.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class AlarmException extends ServiceException {
	public AlarmException(AlarmErrorCode errorCode) {
		super(errorCode);
	}
}
