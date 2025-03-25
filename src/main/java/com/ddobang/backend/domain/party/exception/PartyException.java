package com.ddobang.backend.domain.party.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

public class PartyException extends ServiceException {
    public PartyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
