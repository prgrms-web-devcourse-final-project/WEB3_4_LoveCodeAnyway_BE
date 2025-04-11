package com.ddobang.backend.global.exception.oauth2;

import com.ddobang.backend.global.exception.ServiceException;

public class OAuth2Exception extends ServiceException {
	public OAuth2Exception(OAuth2ErrorCode errorCode) {
		super(errorCode);
	}
}
