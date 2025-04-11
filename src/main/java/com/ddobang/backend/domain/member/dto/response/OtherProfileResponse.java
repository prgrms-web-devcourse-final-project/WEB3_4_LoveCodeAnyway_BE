package com.ddobang.backend.domain.member.dto.response;

import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;

// 다른 회원의 프로필 정보 조회 응답 DTO
public record OtherProfileResponse(
	String nickname,
	Gender gender,
	String introduction,
	String profilePicture,
	int mannerScore,
	int hostCount
) {
	public static OtherProfileResponse of(Member member) {
		return new OtherProfileResponse(
			member.getNickname(),
			member.getGender(),
			member.getIntroduction(),
			member.getProfilePictureUrl(),
			member.getMannerScore(),
			member.getHostCount()
		);
	}
}
