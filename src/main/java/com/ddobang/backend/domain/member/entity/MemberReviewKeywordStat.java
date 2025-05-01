package com.ddobang.backend.domain.member.entity;

import com.ddobang.backend.domain.member.types.MemberReviewKeyword;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class MemberReviewKeywordStat { //키워드 별 통계
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private MemberReview review;

	@Enumerated(EnumType.STRING)
	private MemberReviewKeyword keyword;

	private int count;

	private MemberReviewKeywordStat(MemberReview review, MemberReviewKeyword keyword, int count) {
		this.review = review;
		this.keyword = keyword;
		this.count = count;
	}

	public static MemberReviewKeywordStat of(MemberReview review, MemberReviewKeyword keyword, int count) {
		return new MemberReviewKeywordStat(review, keyword, count);
	}
}
