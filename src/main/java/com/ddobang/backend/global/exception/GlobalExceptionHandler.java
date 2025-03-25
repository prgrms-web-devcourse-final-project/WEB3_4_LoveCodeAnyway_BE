package com.ddobang.backend.global.exception;

import com.ddobang.backend.global.response.ErrorResponse;
import com.ddobang.backend.global.response.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("[ServiceException] status={}, code={}, message={}",
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage());
        return ResponseFactory.error(errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("[UnhandledException] message={}, exception={}",
                e.getMessage(), e.getClass().getSimpleName(), e);
        return ResponseFactory.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
