package com.ddobang.backend.domain.message.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

public class MessageException extends ServiceException {
    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
