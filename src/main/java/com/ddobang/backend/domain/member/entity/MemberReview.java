package com.ddobang.backend.domain.member.entity;

import java.util.List;

import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberReview extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	private Long partyId;

	private Long receiverId;

	private Long reviewerId;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
		name = "member_review_keywords",
		joinColumns = @JoinColumn(name = "review_id")
	)
	@Enumerated(EnumType.STRING)
	@Column(name = "evaluation_keyword")
	private List<EvaluationKeyword> evaluationKeywords;
}
