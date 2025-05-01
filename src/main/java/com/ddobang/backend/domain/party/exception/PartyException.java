package com.ddobang.backend.domain.party.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class PartyException extends ServiceException {
	public PartyException(PartyErrorCode errorCode) {
		super(errorCode);
	}
}
