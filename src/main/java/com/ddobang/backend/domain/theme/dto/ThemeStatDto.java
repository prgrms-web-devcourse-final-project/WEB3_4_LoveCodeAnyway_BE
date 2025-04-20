package com.ddobang.backend.domain.theme.dto;

import com.ddobang.backend.domain.theme.entity.ThemeStat;

import lombok.Builder;

/**
 * ThemeStatsDto
 * 테마 통계 dto
 * @author 100minha
 */
@Builder
public record ThemeStatDto(
	float difficulty,
	float fear,
	float activity,
	float satisfaction,
	float production,
	float story,
	float question,
	float interior,
	float deviceRatio,
	int noHintEscapeRate,
	int escapeResult,
	int escapeTimeAvg
) {
	public static ThemeStatDto of(ThemeStat stats) {
		return ThemeStatDto.builder()
			.difficulty(stats.getDifficulty())
			.fear(stats.getFear())
			.activity(stats.getActivity())
			.satisfaction(stats.getSatisfaction())
			.production(stats.getProduction())
			.story(stats.getStory())
			.question(stats.getQuestion())
			.interior(stats.getInterior())
			.deviceRatio(stats.getDeviceRatio())
			.noHintEscapeRate(stats.getNoHintEscapeRate())
			.escapeResult(stats.getEscapeResult())
			.escapeTimeAvg(stats.getEscapeTimeAvg())
			.build();
	}
}
