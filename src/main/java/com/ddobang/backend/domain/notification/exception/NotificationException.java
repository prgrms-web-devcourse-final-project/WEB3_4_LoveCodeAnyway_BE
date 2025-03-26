package com.ddobang.backend.domain.notification.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class NotificationException extends ServiceException {
    public NotificationException(NotificationErrorCode errorCode) {
        super(errorCode);
    }
}
