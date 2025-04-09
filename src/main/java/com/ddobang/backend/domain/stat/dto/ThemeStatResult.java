package com.ddobang.backend.domain.stat.dto;

import lombok.Builder;

@Builder
public record ThemeStatResult(
	float difficultyAvg,
	float fearAvg,
	float activityAvg,
	float satisfactionAvg,
	float productionAvg,
	float storyAvg,
	float questionAvg,
	float interiorAvg,
	float deviceRatioAvg,
	int noHintEscapeRate,
	int escapedRate,
	int escapeTimeAvg
) {
}
