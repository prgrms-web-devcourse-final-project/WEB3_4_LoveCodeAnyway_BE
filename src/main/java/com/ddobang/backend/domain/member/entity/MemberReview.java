package com.ddobang.backend.domain.member.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ddobang.backend.domain.member.types.MemberReviewKeyword;
import com.ddobang.backend.global.entity.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class MemberReview extends BaseTime { // 누적 리뷰
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private BigDecimal averageScore;
	private Integer totalReviews;
	private Integer positiveCount;
	private Integer negativeCount;
	private Integer noShowCount;

	@OneToMany(mappedBy = "review")
	private List<MemberReviewKeywordStat> keywordStats;

	private MemberReview(Long memberId) {
		this.memberId = memberId;
		this.averageScore = BigDecimal.valueOf(0);
		this.totalReviews = 0;
		this.positiveCount = 0;
		this.negativeCount = 0;
		this.noShowCount = 0;
		this.keywordStats = new ArrayList<>();
	}

	public static MemberReview of(Long memberId) {
		return new MemberReview(memberId);
	}

	public void update(
		BigDecimal averageScore,
		int totalReviews,
		int positiveCount,
		int negativeCount,
		int noShowCount,
		Map<MemberReviewKeyword, Integer> keywordMap
	) {
		this.averageScore = averageScore;
		this.totalReviews = totalReviews;
		this.positiveCount = positiveCount;
		this.negativeCount = negativeCount;
		this.noShowCount = noShowCount;

		this.keywordStats.clear();

		for (Map.Entry<MemberReviewKeyword, Integer> entry : keywordMap.entrySet()) {
			this.keywordStats.add(MemberReviewKeywordStat.of(this, entry.getKey(), entry.getValue()));
		}
	}
}
