package com.ddobang.backend.global.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ddobang.backend.global.validation.validator.EnumValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Enum
 * enum 타입을 검증하기 위한 커스텀 어노테이션
 * @author 100minha
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {
	String message() default "Invalid enum value";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends Enum<?>> enumClass();
}
