package com.ddobang.backend.global.response;

import com.ddobang.backend.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseFactory {
    public static <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    public static <T> ResponseEntity<T> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
