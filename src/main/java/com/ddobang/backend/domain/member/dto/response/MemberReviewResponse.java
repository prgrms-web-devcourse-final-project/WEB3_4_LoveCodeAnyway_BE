package com.ddobang.backend.domain.member.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.ddobang.backend.domain.member.entity.MemberReview;

public record MemberReviewResponse(
	BigDecimal averageScore,
	Integer totalReviews,
	Integer positiveCount,
	Integer negativeCount,
	Integer noShowCount,
	List<KeywordStatResponse> keywords
) {
	public static MemberReviewResponse from(MemberReview memberReview, List<KeywordStatResponse> keywords) {
		return new MemberReviewResponse(
			memberReview.getAverageScore(),
			memberReview.getTotalReviews(),
			memberReview.getPositiveCount(),
			memberReview.getNegativeCount(),
			memberReview.getNoShowCount(),
			keywords
		);
	}
}
