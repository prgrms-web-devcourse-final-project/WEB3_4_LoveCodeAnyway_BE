package com.ddobang.backend.global.exception.auth;

import com.ddobang.backend.global.exception.ServiceException;

public class AuthException extends ServiceException {
	public AuthException(AuthErrorCode errorCode) {
		super(errorCode);
	}
}
