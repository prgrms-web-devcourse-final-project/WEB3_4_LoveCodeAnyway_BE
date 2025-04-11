package com.ddobang.backend.domain.member.dto.stat;

import java.util.Map;

import lombok.Builder;

@Builder
public record EscapeProfileStatDto(
	double tendencyStimulating,
	double tendencyLogical,
	double tendencyNarrative,
	double tendencyActive,
	double tendencySpatial,

	Map<String, Integer> genreCountMap,
	Map<String, Integer> genreSuccessMap,

	double difficultyHintAvg1,
	double difficultyHintAvg2,
	double difficultyHintAvg3,
	double difficultyHintAvg4,
	double difficultyHintAvg5,

	double difficultySatisAvg1,
	double difficultySatisAvg2,
	double difficultySatisAvg3,
	double difficultySatisAvg4,
	double difficultySatisAvg5
) {
}
