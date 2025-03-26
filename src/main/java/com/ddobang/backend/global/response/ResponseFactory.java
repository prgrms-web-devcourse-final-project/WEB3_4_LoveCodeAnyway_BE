package com.ddobang.backend.global.response;

import com.ddobang.backend.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseFactory {
    public static <T> ResponseEntity<SuccessResponse<T>> ok(String message, T data) {
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(message, data));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> created(String message, T data) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(message, data));
    }

    public static ResponseEntity<SuccessResponse<Void>> noContent(String message) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(SuccessResponse.of(message));
    }

    public static ResponseEntity<ErrorResponse> error(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    public static ResponseEntity<ErrorResponse> error(String errorCode, String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(errorCode, message));
    }
}
