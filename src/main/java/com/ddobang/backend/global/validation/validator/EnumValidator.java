package com.ddobang.backend.global.validation.validator;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.ddobang.backend.global.validation.annotation.ValidEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * EnumValidator
 * Enum 타입의 값이 유효한지 검증하는 클래스
 * @author 100minha
 */
public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
	private Set<String> validValues;

	@Override
	public void initialize(ValidEnum constraintAnnotation) {
		validValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
			.map(Enum::name) // Enum 상수 이름만 저장
			.collect(Collectors.toSet());
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true; // null은 별도로 @NotNull 등으로 처리
		}
		return validValues.contains(value.toUpperCase()); // 대소문자 무시
	}
}
