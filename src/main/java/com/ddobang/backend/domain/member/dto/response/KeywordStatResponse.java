package com.ddobang.backend.domain.member.dto.response;

import com.ddobang.backend.domain.member.types.MemberReviewKeyword;

public record KeywordStatResponse(
	MemberReviewKeyword keyword,
	Integer count
) {
	public static KeywordStatResponse from(MemberReviewKeyword keyword, Integer count) {
		return new KeywordStatResponse(keyword, count);
	}
}
