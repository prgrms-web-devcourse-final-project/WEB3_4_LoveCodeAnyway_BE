package com.ddobang.backend.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberException extends RuntimeException {
	private final MemberErrorCode errorCode;

	@Override
	public String getMessage() {
		return errorCode.getMessage();
	}
}
