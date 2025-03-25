package com.ddobang.backend.domain.member.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

public class MemberException extends ServiceException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
