package com.ddobang.backend.global.response;

import java.util.List;

import com.ddobang.backend.global.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ErrorResponse(
	String errorCode,
	String message,
	List<ValidationError> errors) {

	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.builder()
			.errorCode(errorCode.getErrorCode())
			.message(errorCode.getMessage())
			.build();
	}

	// TODO: 사용하지 않는 메소드인지 확인
	public static ErrorResponse of(String errorCode, String message) {
		return ErrorResponse.builder()
			.errorCode(errorCode)
			.message(message)
			.build();
	}

	/**
	 * Validation 예외 발생 시 필드별 예외 정보 관리 클래스
	 * @param field
	 * @param message
	 */
	@Builder
	public record ValidationError(
		String field,    // 에러가 발생한 필드명
		String message    // 해당 필드의 에러 메시지
	) {
	}

	/**
	 * Validation 예외 발생 시 응답 때 사용할 팩토리 메서드
	 * @param errorCode    // 예외 코드
	 * @param errors    // 필드별 에러 메세지
	 */
	public static ErrorResponse of(ErrorCode errorCode, List<ValidationError> errors) {
		return ErrorResponse.builder()
			.errorCode(errorCode.getErrorCode())
			.message(errorCode.getMessage())
			.errors(errors)
			.build();
	}
}
