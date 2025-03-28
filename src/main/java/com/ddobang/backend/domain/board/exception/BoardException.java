package com.ddobang.backend.domain.board.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class BoardException extends ServiceException {
	public BoardException(BoardErrorCode errorCode) {
		super(errorCode);
	}
}
