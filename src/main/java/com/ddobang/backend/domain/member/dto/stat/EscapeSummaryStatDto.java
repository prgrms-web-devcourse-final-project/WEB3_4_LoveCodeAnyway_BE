package com.ddobang.backend.domain.member.dto.stat;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record EscapeSummaryStatDto(
	int totalCount,
	double successRate,
	int noHintSuccessCount,
	double noHintSuccessRate,
	double averageHintCount,

	LocalDate firstEscapeDate,
	String mostActiveMonth,
	int mostActiveMonthCount,
	Integer daysSinceFirstEscape
) {
}
