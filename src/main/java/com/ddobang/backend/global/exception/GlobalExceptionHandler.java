package com.ddobang.backend.global.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.ddobang.backend.domain.alarm.exception.SseException;
import com.ddobang.backend.global.response.ErrorResponse;
import com.ddobang.backend.global.response.ResponseFactory;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.warn("[ServiceException] status={}, code={}, message={}",
			errorCode.getStatus().value(),
			errorCode.getErrorCode(),
			errorCode.getMessage());
		return ResponseFactory.error(errorCode);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
		log.error("[UnhandledException] message={}, exception={}",
			e.getMessage(), e.getClass().getSimpleName(), e);
		return ResponseFactory.error(GlobalErrorCode.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Valid 검증 실패 예외 핸들러 메소드
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		List<ErrorResponse.ValidationError> errors = new ArrayList<>();

		ex.getBindingResult().getAllErrors().forEach(error -> {
			errors.add(ErrorResponse.ValidationError.builder()
				.field(((FieldError)error).getField())
				.message(error.getDefaultMessage())
				.build());
		});

		log.warn("[ValidationException] errorFields={}", errors);

		return ResponseFactory.error(GlobalErrorCode.NOT_VALID, errors);
	}

	/**
	 * Valid 검증 실패 예외 핸들러 메소드
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		List<ErrorResponse.ValidationError> errors = new ArrayList<>();

		ex.getConstraintViolations().forEach(violation -> {
			String fieldName = violation.getPropertyPath().toString();
			String message = violation.getMessage();
			errors.add(ErrorResponse.ValidationError.builder()
				.field(fieldName)
				.message(message)
				.build());
		});

		log.warn("[ValidationException] errorFields={}", errors);

		return ResponseFactory.error(GlobalErrorCode.NOT_VALID, errors);
	}

	@ExceptionHandler(SseException.class)
	public ResponseEntity<ErrorResponse> handleSseException(SseException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.warn("[SseException] status={}, code={}, message={}",
			errorCode.getStatus().value(),
			errorCode.getErrorCode(),
			errorCode.getMessage());

		if (e.getCauseIoException() != null) {
			log.debug("Caused by IOException: {}", e.getCauseIoException().getMessage());
		}

		return ResponseFactory.error(errorCode);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		ErrorCode errorCode = GlobalErrorCode.INVALID_REQUEST;

		log.warn("[MethodArgumentTypeMismatchException] status={}, code={}, message={}",
			errorCode.getStatus().value(),
			errorCode.getErrorCode(),
			errorCode.getMessage());

		return ResponseFactory.error(errorCode);
	}

	@ExceptionHandler(OptimisticLockingFailureException.class)
	public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(OptimisticLockingFailureException e) {
		ErrorCode errorCode = GlobalErrorCode.OPTIMISTIC_LOCKING_FAILURE;
		log.warn("[OptimisticLockingFailureException] status={}, code={}, message={}",
			errorCode.getStatus().value(),
			errorCode.getErrorCode(),
			errorCode.getMessage());

		return ResponseFactory.error(errorCode);
	}
}
