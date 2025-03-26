package com.ddobang.backend.domain.diary.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class DiaryException extends ServiceException {
    public DiaryException(DiaryErrorCode errorCode) {
        super(errorCode);
    }
}
