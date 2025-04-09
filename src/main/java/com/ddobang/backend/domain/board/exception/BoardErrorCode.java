package com.ddobang.backend.domain.board.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum BoardErrorCode implements ErrorCode {
	// Post
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_001", "게시글을 찾을 수 없습니다."),
	POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "POST_002", "게시글에 접근할 수 없습니다."),
	CANNOT_DELETE_NOT_SOFT_DELETED_POST(HttpStatus.BAD_REQUEST, "POST_003", "사용자가 삭제하지 않은 게시글은 삭제할 수 없습니다."),
	POST_HAS_REPLIES(HttpStatus.BAD_REQUEST, "POST_004", "답변이 존재하는 게시글은 삭제할 수 없습니다."),

	// Post Reply
	POST_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_REPLY_001", "답변을 찾을 수 없습니다."),
	POST_REPLY_INVALID_RELATION(HttpStatus.BAD_REQUEST, "POST_REPLY_002", "해당 게시글에 대한 답변이 아닙니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	BoardErrorCode(HttpStatus httpStatus, String errorCode, String message) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
