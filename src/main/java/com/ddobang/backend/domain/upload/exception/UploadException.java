package com.ddobang.backend.domain.upload.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class UploadException extends ServiceException {
    public UploadException(UploadErrorCode errorCode) {
        super(errorCode);
    }
}
