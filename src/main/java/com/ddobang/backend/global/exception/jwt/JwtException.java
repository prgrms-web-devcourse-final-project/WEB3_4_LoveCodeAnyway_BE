package com.ddobang.backend.global.exception.jwt;

import com.ddobang.backend.global.exception.ServiceException;

public class JwtException extends ServiceException {
	public JwtException(JwtErrorCode errorCode) {
		super(errorCode);
	}
}
