package com.ddobang.backend.domain.member.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.member.dto.response.KeywordStatResponse;
import com.ddobang.backend.domain.member.dto.response.MemberReviewResponse;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberReview;
import com.ddobang.backend.domain.member.exception.MemberErrorCode;
import com.ddobang.backend.domain.member.exception.MemberException;
import com.ddobang.backend.domain.member.repository.MemberReviewRepository;
import com.ddobang.backend.domain.member.types.KeywordType;
import com.ddobang.backend.domain.member.types.MemberReviewKeyword;
import com.ddobang.backend.domain.party.entity.PartyMemberReview;
import com.ddobang.backend.domain.party.entity.PartyMemberReviewKeyword;
import com.ddobang.backend.domain.party.repository.PartyMemberReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberReviewService {
	private final PartyMemberReviewRepository reviewRepository;
	private final MemberReviewRepository memberReviewRepository;
	private final MemberService memberService;

	@Transactional
	public void updateMemberReview(Long memberId) {
		List<PartyMemberReview> reviews = reviewRepository.findByReceiverId(memberId);

		int totalReviews = reviews.size();
		int totalScore = 0;
		int positiveCount = 0;
		int negativeCount = 0;

		int noShowCount = 0;

		Map<MemberReviewKeyword, Integer> keywordCountMap = new HashMap<>();

		for (PartyMemberReview review : reviews) {
			boolean hasNoShow = review.getKeywords().stream()
				.anyMatch(k -> k.getKeyword().getType() == KeywordType.NOSHOW);

			if (hasNoShow) {
				noShowCount++;
				continue;
			}

			int score = 50; // 기본 점수 50점부터 시작

			for (PartyMemberReviewKeyword keywordMapping : review.getKeywords()) {
				MemberReviewKeyword keyword = keywordMapping.getKeyword();

				keywordCountMap.merge(keyword, 1, Integer::sum);

				if (keyword.getType() == KeywordType.POSITIVE) {
					positiveCount++;
					score++;
				} else {
					negativeCount++;
					score--;
				}
			}

			totalScore += Math.max(0, Math.min(100, score));
		}

		BigDecimal averageScore = totalReviews == 0
			? BigDecimal.ZERO
			: BigDecimal.valueOf(totalScore)
			.divide(BigDecimal.valueOf(totalReviews), 1, RoundingMode.HALF_UP);

		MemberReview summary = memberReviewRepository.findById(memberId)
			.orElseGet(() -> MemberReview.of(memberId));

		summary.update(averageScore, totalReviews, positiveCount, negativeCount, noShowCount, keywordCountMap);

		Member member = memberService.getMember(memberId);
		member.updateMannerScore(averageScore);

		memberReviewRepository.save(summary);
	}

	public MemberReviewResponse getMemberReview(Long memberId) {
		MemberReview review = memberReviewRepository.findById(memberId).orElseThrow(
			() -> new MemberException(MemberErrorCode.MEMBER_REVIEW_NOT_FOUND));

		List<KeywordStatResponse> keywords = review.getKeywordStats().stream()
			.sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
			.limit(3)
			.map(stat -> KeywordStatResponse.from(
				stat.getKeyword(),
				stat.getCount()))
			.toList();

		return MemberReviewResponse.from(review, keywords);
	}
}
