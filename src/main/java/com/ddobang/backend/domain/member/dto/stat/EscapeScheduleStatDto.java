package com.ddobang.backend.domain.member.dto.stat;

import java.util.Map;

import lombok.Builder;

@Builder
public record EscapeScheduleStatDto(
	Map<String, Integer> monthlyCountMap,

	int lastMonthCount,
	double lastMonthAvgSatisfaction,
	double lastMonthAvgHintCount,
	double lastMonthSuccessRate,
	int lastMonthAvgTime,
	String lastMonthTopTheme,
	int lastMonthTopSatisfaction
) {
}
