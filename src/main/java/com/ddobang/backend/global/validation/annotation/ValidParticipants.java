package com.ddobang.backend.global.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ddobang.backend.global.validation.validator.ParticipantsValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE}) // 클래스
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
@Constraint(validatedBy = ParticipantsValidator.class) // 실제 유효성 검사를 수행할 클래스 지정
public @interface ValidParticipants {
	String message() default "participantsNeeded는 totalParticipants보다 작거나 같아야 합니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
