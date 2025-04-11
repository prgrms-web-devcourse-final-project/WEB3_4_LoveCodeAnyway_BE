package com.ddobang.backend.domain.party.entity;

import java.util.ArrayList;
import java.util.List;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.types.MemberReviewKeyword;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class PartyMemberReview extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private Party party;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private Member receiver;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reviewer_id")
	private Member reviewer;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PartyMemberReviewKeyword> keywords;

	private PartyMemberReview(Party party, Member receiver, Member reviewer) {
		this.party = party;
		this.receiver = receiver;
		this.reviewer = reviewer;
		this.keywords = new ArrayList<>();
	}

	public static PartyMemberReview of(Party party, Member receiver, Member reviewer) {
		return new PartyMemberReview(party, receiver, reviewer);
	}

	public void addKeyword(MemberReviewKeyword keyword) {
		PartyMemberReviewKeyword partyMemberReviewKeyword = PartyMemberReviewKeyword.of(this, keyword);
		this.keywords.add(partyMemberReviewKeyword);
	}
}
