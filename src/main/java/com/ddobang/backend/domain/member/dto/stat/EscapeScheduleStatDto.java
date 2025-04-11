package com.ddobang.backend.domain.member.dto.stat;

import java.util.Map;

import lombok.Builder;

@Builder
public record EscapeScheduleStatDto(
	Map<String, Integer> monthlyCountMap,

	int thisMonthCount,
	double thisMonthAvgSatisfaction,
	double thisMonthAvgHintCount,
	double thisMonthSuccessRate,
	int thisMonthAvgTime,
	String thisMonthTopTheme,
	int thisMonthTopSatisfaction
) {
}
