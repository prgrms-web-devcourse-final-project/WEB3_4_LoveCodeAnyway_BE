package com.ddobang.backend.domain.party.entity;

import com.ddobang.backend.domain.member.types.MemberReviewKeyword;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class PartyMemberReviewKeyword {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private PartyMemberReview review;

	@Enumerated(EnumType.STRING)
	private MemberReviewKeyword keyword;

	private PartyMemberReviewKeyword(PartyMemberReview review, MemberReviewKeyword keyword) {
		this.review = review;
		this.keyword = keyword;
	}

	public static PartyMemberReviewKeyword of(PartyMemberReview review, MemberReviewKeyword keyword) {
		return new PartyMemberReviewKeyword(review, keyword);
	}
}
