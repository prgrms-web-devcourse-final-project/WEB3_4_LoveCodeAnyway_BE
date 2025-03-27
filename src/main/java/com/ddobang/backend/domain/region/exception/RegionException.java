package com.ddobang.backend.domain.region.exception;

import com.ddobang.backend.global.exception.ServiceException;

/**
 * RegionException
 * <p></p>
 * @author 100minha
 */
public class RegionException extends ServiceException {
	public RegionException(RegionErrorCode errorCode) {
		super(errorCode);
	}
}
