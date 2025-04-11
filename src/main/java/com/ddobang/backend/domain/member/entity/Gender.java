package com.ddobang.backend.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
	MALE("남성"),
	FEMALE("여성"),
	BLIND("공개안함");

	private final String description;

	@JsonValue
	public String getDescription() {
		return description;
	}

	@JsonCreator
	public static Gender from(String value) {
		for (Gender gender : values()) {
			if (gender.name().equalsIgnoreCase(value) || gender.description.equals(value)) {
				return gender;
			}
		}
		throw new IllegalArgumentException(String.format("올바르지 않은 성별 값입니다: %s", value));
	}
}
