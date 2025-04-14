package com.ddobang.backend.domain.member.dto.stat;

import com.ddobang.backend.domain.member.entity.EscapeSummaryStat;

// 프로필용 탈출 통계 요약 DTO
public record EscapeProfileSummaryDto(
	int totalCount, // 총 방탈출 횟수
	double successRate, // 탈출 성공률
	double noHintSuccessRate // 노힌트 성공률
) {
	public static EscapeProfileSummaryDto from(EscapeSummaryStat stat) {
		return new EscapeProfileSummaryDto(
			stat.getTotalCount(),
			stat.getSuccessRate(),
			stat.getNoHintSuccessRate()
		);
	}
}
