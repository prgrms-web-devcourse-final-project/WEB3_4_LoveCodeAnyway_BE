package com.ddobang.backend.domain.theme.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

public class ThemeException extends ServiceException {
    public ThemeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
