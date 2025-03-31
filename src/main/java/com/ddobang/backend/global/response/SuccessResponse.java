package com.ddobang.backend.global.response;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SuccessResponse<T>(String message, T data) {

	public static <T> SuccessResponse<T> of(String message) {
		return SuccessResponse.<T>builder()
			.message(message)
			.build();
	}

	public static <T> SuccessResponse<T> of(T data) {
		return SuccessResponse.<T>builder()
			.data(data)
			.build();
	}

	public static <T> SuccessResponse<T> of(String message, T data) {
		return SuccessResponse.<T>builder()
			.message(message)
			.data(data)
			.build();
	}
}
