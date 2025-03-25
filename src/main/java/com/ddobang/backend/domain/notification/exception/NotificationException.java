package com.ddobang.backend.domain.notification.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

public class NotificationException extends ServiceException {
    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
