package com.ddobang.backend.domain.party.exception;

import org.springframework.http.HttpStatus;

import com.ddobang.backend.global.exception.ErrorCode;

public enum PartyErrorCode implements ErrorCode {
	// Party
	PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTY_001", "모임을 찾을 수 없습니다."),
	PARTY_ALREADY_DELETED(HttpStatus.GONE, "PARTY_002", "삭제된 모임입니다."),
	PARTY_HOST_NOT_FOUND(HttpStatus.UNPROCESSABLE_ENTITY, "PARTY_003", "모임에 모임장이 존재하지 않습니다."),
	PARTY_INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "PARTY_004", "현재 상태에서는 모임 상태를 변경할 수 없습니다."),
	PARTY_INVALID_PARTICIPANTS(HttpStatus.BAD_REQUEST, "PARTY_005", "모집 인원은 총 인원보다 많을 수 없습니다."),
	PARTY_FULL(HttpStatus.BAD_REQUEST, "PARTY_006", "모임 인원이 이미 가득 찼습니다."),

	// Party Member

	PARTY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTY_MEMBER_001", "모임원이 아닙니다."),
	PARTY_MEMBER_NOT_HOST(HttpStatus.BAD_REQUEST, "PARTY_MEMBER_002", "이 모임의 모임장이 아닙니다."),
	PARTY_MEMBER_NOT_APPLICANT(HttpStatus.BAD_REQUEST, "PARTY_MEMBER_003", "이 모임의 신청자가 아닙니다."),
	PARTY_MEMBER_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "PARTY_MEMBER_004", "이 모임의 승인된 모임원이 아닙니다."),
	PARTY_MEMBER_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "PARTY_MEMBER_005", "이미 신청한 모임입니다."),
	PARTY_MEMBER_ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "PARTY_MEMBER_006", "이미 승인된 모임원입니다.");

	private final HttpStatus httpStatus;
	private final String errorCode;
	private final String message;

	PartyErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
