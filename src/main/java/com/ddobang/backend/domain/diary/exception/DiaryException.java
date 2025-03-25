package com.ddobang.backend.domain.diary.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

public class DiaryException extends ServiceException {
    public DiaryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
