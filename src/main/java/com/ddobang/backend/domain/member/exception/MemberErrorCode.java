package com.ddobang.backend.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum MemberErrorCode implements ErrorCode {
	// Member
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "해당 사용자를 찾을 수 없습니다."),
	DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER_002", "이미 존재하는 닉네임입니다."),
	MEMBER_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER_003", "회원 탈퇴에 실패했습니다."),

	// Member Tag
	MEMBER_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_TAG_001", "멤버 태그를 찾을 수 없습니다."),

	// Member Review
	MEMBER_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_001", "멤버 리뷰를 찾을 수 없습니다."),
	MEMBER_REVIEW_DUPLICATED(HttpStatus.BAD_REQUEST, "REVIEW_002", "이미 멤버 리뷰를 작성하셨습니다."),

	// Member Stat
	NOT_FOUND_STAT(HttpStatus.NOT_FOUND, "MEMBER_404", "해당 사용자의 통계 정보가 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	MemberErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
