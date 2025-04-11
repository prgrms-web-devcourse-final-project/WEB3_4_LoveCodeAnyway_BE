package com.ddobang.backend.domain.member.entity;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
	MALE("남성"),
	FEMALE("여성"),
	BLIND("공개안함");

	private final String label;

	@JsonCreator
	public static Gender from(String input) {
		return Arrays.stream(values())
			.filter(g -> g.name().equalsIgnoreCase(input) || g.label.equals(input))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("성별 값이 유효하지 않습니다: " + input));
	}

}
