package com.ddobang.backend.domain.member.dto.response;

import java.math.BigDecimal;

import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;

public record BasicProfileResponse(
	String nickname,
	Gender gender,
	String introduction,
	String profilePictureUrl,
	BigDecimal mannerScore
) {
	public static BasicProfileResponse of(Member member) {
		return new BasicProfileResponse(
			member.getNickname(),
			member.getGender(),
			member.getIntroduction(),
			member.getProfilePictureUrl(),
			member.getMannerScore()
		);
	}
}
