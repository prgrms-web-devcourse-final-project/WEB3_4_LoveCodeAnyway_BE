package com.ddobang.backend.global.auth.dto.request;

import java.util.List;

import com.ddobang.backend.domain.member.entity.Gender;
import com.ddobang.backend.domain.member.entity.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// 회원가입 요청 DTO
public record SignupRequest(
	@NotBlank(message = "닉네임은 필수입니다.")
	String nickname,

	@NotNull(message = "성별은 필수입니다.")
	Gender gender,

	@Size(max = 200, message = "자기소개는 200자 이내로 입력해주세요.")
	String introduction,

	// 태그는 선택 사항
	@Size(max = 5, message = "태그는 최대 5개까지 선택할 수 있습니다.")
	List<Long> tags, // 태그 ID 리스트

	// 프로필 사진은 선택 사항
	String profilePictureUrl
) {
	public Member toEntity(String kakaoId) {
		return Member.builder()
			.kakaoId(kakaoId)
			.nickname(nickname)
			.gender(gender)
			.introduction(introduction)
			.profilePictureUrl(profilePictureUrl)
			.build();
	}
}
