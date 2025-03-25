package com.ddobang.backend.domain.board.exception;

import com.ddobang.backend.global.exception.ErrorCode;
import com.ddobang.backend.global.exception.ServiceException;

public class BoardException extends ServiceException {
    public BoardException(ErrorCode errorCode) {
        super(errorCode);
    }
}
