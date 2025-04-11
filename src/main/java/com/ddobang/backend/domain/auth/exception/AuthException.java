package com.ddobang.backend.domain.auth.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class AuthException extends ServiceException {
	public AuthException(OAuth2ErrorCode errorCode) {
		super(errorCode);
	}
}
