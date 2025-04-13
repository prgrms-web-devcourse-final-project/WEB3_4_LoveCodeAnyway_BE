package com.ddobang.backend.domain.member.dto.request;

// 닉네임, 소개글, 프로필 이미지 URL을 포함하는 프로필 업데이트 요청 DTO
// 각 필드는 null일 수 있으며, null인 경우 해당 필드는 업데이트되지 않음
public record UpdateProfileRequest(
	String nickname,
	String introduction,
	String profileImageUrl
) {

}
