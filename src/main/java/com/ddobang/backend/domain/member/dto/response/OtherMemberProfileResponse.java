package com.ddobang.backend.domain.member.dto.response;

import java.util.List;

import com.ddobang.backend.domain.member.dto.stat.EscapeProfileSummaryDto;

// 다른 회원의 프로필 조회 응답 DTO
public record OtherMemberProfileResponse(
	OtherProfileResponse profile, // 회원 프로필 정보
	List<MemberTagResponse> tags, // 회원 태그 목록
	EscapeProfileSummaryDto stats // 회원 통계 요약 정보
) {
	public static OtherMemberProfileResponse of(
		OtherProfileResponse profile,
		List<MemberTagResponse> tags,
		EscapeProfileSummaryDto stats
	) {
		return new OtherMemberProfileResponse(profile, tags, stats);
	}
}
