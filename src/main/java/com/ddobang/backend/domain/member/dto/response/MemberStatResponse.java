package com.ddobang.backend.domain.member.dto.response;

import java.time.LocalDate;
import java.util.Map;

import com.ddobang.backend.domain.member.entity.EscapeProfileStat;
import com.ddobang.backend.domain.member.entity.EscapeScheduleStat;
import com.ddobang.backend.domain.member.entity.EscapeSummaryStat;

import lombok.Builder;

@Builder
public record MemberStatResponse(
	int totalCount,
	double successRate,
	int noHintSuccessCount,
	double noHintSuccessRate,
	double averageHintCount,
	Map<String, Integer> genreCountMap,
	Map<String, Integer> genreSuccessMap,
	Map<String, Double> tendencyMap,
	Map<String, Integer> monthlyCountMap,
	LocalDate firstEscapeDate,
	String mostActiveMonth,
	int mostActiveMonthCount,
	int daysSinceFirstEscape,
	LastMonthInfo lastMonthInfo,
	Map<Integer, Double> difficultyHintAvgMap,
	Map<Integer, Double> difficultySatisAvgMap
) {
	public static MemberStatResponse of(
		EscapeSummaryStat escapeSummaryStat,
		EscapeProfileStat escapeProfileStat,
		EscapeScheduleStat escapeScheduleStat
	) {
		return MemberStatResponse.builder()
			.totalCount(escapeSummaryStat.getTotalCount())
			.successRate(escapeSummaryStat.getSuccessRate())
			.noHintSuccessCount(escapeSummaryStat.getNoHintSuccessCount())
			.noHintSuccessRate(escapeSummaryStat.getNoHintSuccessRate())
			.averageHintCount(escapeSummaryStat.getAverageHintCount())
			.genreCountMap(escapeProfileStat.getGenreCountMap())
			.genreSuccessMap(escapeProfileStat.getGenreSuccessMap())
			.tendencyMap(Map.of(
				"tendencyStimulating",
				escapeProfileStat.getTendencyStimulating(),
				"tendencyLogical",
				escapeProfileStat.getTendencyLogical(),
				"tendencyNarrative",
				escapeProfileStat.getTendencyNarrative(),
				"tendencyActive",
				escapeProfileStat.getTendencyActive(),
				"tendencySpatial",
				escapeProfileStat.getTendencySpatial()
			))
			.monthlyCountMap(escapeScheduleStat.getMonthlyCountMap())
			.firstEscapeDate(escapeSummaryStat.getFirstEscapeDate())
			.mostActiveMonth(escapeSummaryStat.getMostActiveMonth())
			.mostActiveMonthCount(escapeSummaryStat.getMostActiveMonthCount())
			.daysSinceFirstEscape(escapeSummaryStat.getDaysSinceFirstEscape())
			.lastMonthInfo(LastMonthInfo.of(escapeScheduleStat))
			.difficultyHintAvgMap(Map.of(
				1, escapeProfileStat.getDifficultyHintAvg1(),
				2, escapeProfileStat.getDifficultyHintAvg2(),
				3, escapeProfileStat.getDifficultyHintAvg3(),
				4, escapeProfileStat.getDifficultyHintAvg4(),
				5, escapeProfileStat.getDifficultyHintAvg5()
			))
			.difficultySatisAvgMap(Map.of(
				1, escapeProfileStat.getDifficultySatisAvg1(),
				2, escapeProfileStat.getDifficultySatisAvg2(),
				3, escapeProfileStat.getDifficultySatisAvg3(),
				4, escapeProfileStat.getDifficultySatisAvg4(),
				5, escapeProfileStat.getDifficultySatisAvg5()
			))
			.build();
	}

	public record LastMonthInfo(
		int lastMonthCount,
		double lastMonthAvgSatisfaction,
		double lastMonthAvgHintCount,
		double lastMonthSuccessRate,
		int lastMonthAvgTime,
		String lastMonthTopTheme,
		int lastMonthTopSatisfaction
	) {
		public static MemberStatResponse.LastMonthInfo of(EscapeScheduleStat escapeScheduleStat) {
			return new MemberStatResponse.LastMonthInfo(
				escapeScheduleStat.getLastMonthCount(),
				escapeScheduleStat.getLastMonthAvgSatisfaction(),
				escapeScheduleStat.getLastMonthAvgHintCount(),
				escapeScheduleStat.getLastMonthSuccessRate(),
				escapeScheduleStat.getLastMonthAvgTime(),
				escapeScheduleStat.getLastMonthTopTheme(),
				escapeScheduleStat.getLastMonthTopSatisfaction()
			);
		}
	}
}
