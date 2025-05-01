package com.ddobang.backend.global.response;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ddobang.backend.global.exception.ErrorCode;

public class ResponseFactory {

	/*
	메세지, 데이터 모두 포함 (200 OK)
	 */
	public static <T> ResponseEntity<SuccessResponse<T>> ok(String message, T data) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(SuccessResponse.of(message, data));
	}

	/*
	메세지 포함 (200 OK)
	 */
	public static ResponseEntity<SuccessResponse<Void>> ok(String message) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(SuccessResponse.of(message));
	}

	/*
	데이터 포함 (200 OK)
	 */
	public static <T> ResponseEntity<SuccessResponse<T>> ok(T data) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(SuccessResponse.of(data));
	}

	/*
	메세지, 데이터 모두 포함 (201 Created)
	 */
	public static <T> ResponseEntity<SuccessResponse<T>> created(String message, T data) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(SuccessResponse.of(message, data));
	}

	/*
	메세지 포함 (201 Created)
	 */
	public static ResponseEntity<SuccessResponse<Void>> created(String message) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(SuccessResponse.of(message));
	}

	/*
	데이터 포함 (201 Created)
	 */
	public static <T> ResponseEntity<SuccessResponse<T>> created(T data) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(SuccessResponse.of(data));
	}

	public static ResponseEntity<Void> noContent() {
		return ResponseEntity.noContent().build();
	}

	public static ResponseEntity<ErrorResponse> error(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode));
	}

	public static ResponseEntity<ErrorResponse> error(ErrorCode errorCode, List<ErrorResponse.ValidationError> errors) {
		return ResponseEntity.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode, errors));
	}
}
