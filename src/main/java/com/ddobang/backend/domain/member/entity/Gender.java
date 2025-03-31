package com.ddobang.backend.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
	MALE("남성"),
	FEMALE("여성"),
	OTHER("기타");

	private final String displayName;
}
