package com.ddobang.backend.domain.theme.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class ThemeException extends ServiceException {
    public ThemeException(ThemeErrorCode errorCode) {
        super(errorCode);
    }
}
