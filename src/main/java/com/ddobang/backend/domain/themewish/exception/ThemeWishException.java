package com.ddobang.backend.domain.themewish.exception;

import com.ddobang.backend.global.exception.ServiceException;

/**
 * ThemeWishException
 * <p></p>
 * @author 100minha
 */
public class ThemeWishException extends ServiceException {
	public ThemeWishException(ThemeWishErrorCode errorCode) {
		super(errorCode);
	}
}
