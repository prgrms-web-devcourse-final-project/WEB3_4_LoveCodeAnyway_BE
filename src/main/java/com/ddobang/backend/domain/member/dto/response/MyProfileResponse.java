package com.ddobang.backend.domain.member.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberStat;

public record MyProfileResponse(
	Long memberId,
	String nickname,
	String profileImageUrl,
	String introduce,
	BigDecimal mannerScore,
	Integer hostCount,
	List<MemberTagResponse> tags,
	int escapeCount,
	double successRate,
	double noHintSuccessRate
) {
	public static MyProfileResponse of(Member member, MemberStat stat, List<MemberTagResponse> tags) {
		return new MyProfileResponse(
			member.getId(),
			member.getNickname(),
			member.getProfilePictureUrl(),
			member.getIntroduction(),
			member.getMannerScore(),
			member.getHostCount(),
			tags,
			stat.getEscapeSummaryStat().getTotalCount(),
			stat.getEscapeSummaryStat().getSuccessRate(),
			stat.getEscapeSummaryStat().getNoHintSuccessRate()
		);
	}
}
