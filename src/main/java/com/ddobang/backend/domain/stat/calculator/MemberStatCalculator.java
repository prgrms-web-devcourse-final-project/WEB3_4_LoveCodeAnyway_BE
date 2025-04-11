package com.ddobang.backend.domain.stat.calculator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.diary.entity.DiaryStat;
import com.ddobang.backend.domain.diary.repository.DiaryStatRepository;
import com.ddobang.backend.domain.member.dto.stat.EscapeProfileStatDto;
import com.ddobang.backend.domain.member.dto.stat.EscapeSummaryStatDto;
import com.ddobang.backend.global.util.Ut;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberStatCalculator {
	private final DiaryStatRepository diaryStatRepository;

	public void updateThemeStat() {
		List<DiaryStat> diaryStats = diaryStatRepository.findByAuthorId(1L);
	}

	// EscapeSummaryStat 계산 메서드
	private EscapeSummaryStatDto calculateEscapeSummaryStat(List<DiaryStat> diaryStats) {
		int totalCount = diaryStats.size();

		int escapeSuccessCount = 0;
		int noHintSuccessCount = 0;
		int hintCount = 0;
		int totalHintCount = 0;
		LocalDate earliestEscapeDate = null;
		Integer daysSinceFirstEscape = null;
		Map<YearMonth, Integer> monthCountMap = new HashMap<>();

		for (DiaryStat stat : diaryStats) {
			if (stat.isEscapeResult()) {
				escapeSuccessCount++;
				if (stat.getHintCount() == 0) {
					noHintSuccessCount++;
				}
			}

			if (stat.getHintCount() != null) {
				hintCount++;
				totalHintCount += stat.getHintCount();
			}

			LocalDate date = stat.getDiary().getEscapeDate();

			if (date != null) {
				if (earliestEscapeDate == null || date.isBefore(earliestEscapeDate)) {
					earliestEscapeDate = date;
				}
				YearMonth yearMonth = YearMonth.from(date);
				monthCountMap.put(yearMonth, monthCountMap.getOrDefault(yearMonth, 0) + 1);
			}
		}

		double successRate = Ut.calculator.roundToFirstDecimalAsDouble(
			Ut.calculator.calculateRate(totalCount, escapeSuccessCount)
		);

		double noHintSuccessRate = Ut.calculator.roundToFirstDecimalAsDouble(
			Ut.calculator.calculateRate(totalCount, noHintSuccessCount)
		);

		double averageHintCount = Ut.calculator.roundToFirstDecimalAsDouble(
			Ut.calculator.calculateAverage(totalHintCount, hintCount)
		);

		if (earliestEscapeDate != null) {
			daysSinceFirstEscape = (int)ChronoUnit.DAYS.between(earliestEscapeDate, LocalDate.now());
		}

		Map.Entry<YearMonth, Integer> mostActiveMonth =
			monthCountMap.entrySet()
				.stream()
				.max(Map.Entry.comparingByValue())
				.orElse(null);

		return EscapeSummaryStatDto.builder()
			.totalCount(totalCount)
			.successRate(successRate)
			.noHintSuccessCount(noHintSuccessCount)
			.noHintSuccessRate(noHintSuccessRate)
			.averageHintCount(averageHintCount)
			.firstEscapeDate(earliestEscapeDate)
			.mostActiveMonth(
				mostActiveMonth != null
					? mostActiveMonth.getKey().format(DateTimeFormatter.ofPattern("yyyy년 M월")) : null
			)
			.mostActiveMonthCount(
				mostActiveMonth != null
					? mostActiveMonth.getValue() : 0
			)
			.daysSinceFirstEscape(daysSinceFirstEscape)
			.build();
	}

	// EscapeProfileStat 계산 메서드
	private EscapeProfileStatDto calculateEscapeProfileStat(List<DiaryStat> diaryStats, long authorId) {
		// 자극형 - 공포도, 연출 각각의 가중치
		double tendencyStimulating = calculateTendencyScore(diaryStats, Map.of(
			"fear", 0.6,
			"production", 0.4
		));

		// 논리형 - 문제, 난이도 각각의 가중치
		double tendencyLogical = calculateTendencyScore(diaryStats, Map.of(
			"question", 0.65,
			"difficulty", 0.35
		));

		// 서사형 - 스토리, 연출 각각의 가중치
		double tendencyNarrative = calculateTendencyScore(diaryStats, Map.of(
			"story", 0.7,
			"production", 0.3
		));

		// 활동형 - 활동형 가중치
		double tendencyActive = calculateTendencyScore(diaryStats, Map.of(
			"activity", 1.0
		));

		// 공간중시형 - 인테리어, 연출, 스토리 각각의 가중치
		double tendencySpatial = calculateTendencyScore(diaryStats, Map.of(
			"interior", 0.5,
			"production", 0.3,
			"story", 0.2
		));

		Map<String, Integer> genreCountMap = new HashMap<>();
		Map<String, Integer> genreSuccessMap = new HashMap<>();
		// 장르명, 해당 장르 플레이 갯수, 해당 장르 성공 플레이 갯수
		List<Tuple> top5TagInfo =
			diaryStatRepository.top5TagCountSuccessCountByMember(authorId);
		// 플레이한 테마들의 모든 장르 수 (중복 포함) + 장르가 입력되지 않은 테마의 수
		long totalTagCount =
			diaryStatRepository.countTotalGenreBaseByMember(authorId);

		for (Tuple tuple : top5TagInfo) {
			genreCountMap.put(
				tuple.get(0, String.class),
				Ut.calculator.roundToInt(
					Ut.calculator.calculateRate(totalTagCount, tuple.get(1, Long.class))
				)
			);

			genreSuccessMap.put(
				tuple.get(0, String.class),
				Ut.calculator.roundToInt(
					Ut.calculator.calculateRate(tuple.get(1, Long.class), tuple.get(2, Integer.class))
				)
			);
		}

		Map<Integer, Tuple> difficultyWithHints = diaryStatRepository.difficultyStatsWithHints(authorId);
		Map<Integer, Tuple> difficultyWithSatis =
			diaryStatRepository.difficultyStatsWithSatisfaction(authorId);

		double difficultyHintAvg1 =
			calculateAvgFromTuple(difficultyWithHints, 1, 1, 0);
		double difficultyHintAvg2 =
			calculateAvgFromTuple(difficultyWithHints, 2, 1, 0);
		double difficultyHintAvg3 =
			calculateAvgFromTuple(difficultyWithHints, 3, 1, 0);
		double difficultyHintAvg4 =
			calculateAvgFromTuple(difficultyWithHints, 4, 1, 0);
		double difficultyHintAvg5 =
			calculateAvgFromTuple(difficultyWithHints, 5, 1, 0);

		double difficultySatisAvg1 =
			calculateAvgFromTuple(difficultyWithSatis, 1, 1, 0);
		double difficultySatisAvg2 =
			calculateAvgFromTuple(difficultyWithSatis, 2, 1, 0);
		double difficultySatisAvg3 =
			calculateAvgFromTuple(difficultyWithSatis, 3, 1, 0);
		double difficultySatisAvg4 =
			calculateAvgFromTuple(difficultyWithSatis, 4, 1, 0);
		double difficultySatisAvg5 =
			calculateAvgFromTuple(difficultyWithSatis, 5, 1, 0);

		return EscapeProfileStatDto.builder()
			.tendencyStimulating(tendencyStimulating)
			.tendencyLogical(tendencyLogical)
			.tendencyNarrative(tendencyNarrative)
			.tendencyActive(tendencyActive)
			.tendencySpatial(tendencySpatial)
			.genreCountMap(genreCountMap)
			.genreSuccessMap(genreSuccessMap)
			.difficultyHintAvg1(difficultyHintAvg1)
			.difficultyHintAvg2(difficultyHintAvg2)
			.difficultyHintAvg3(difficultyHintAvg3)
			.difficultyHintAvg4(difficultyHintAvg4)
			.difficultyHintAvg5(difficultyHintAvg5)
			.difficultySatisAvg1(difficultySatisAvg1)
			.difficultySatisAvg2(difficultySatisAvg2)
			.difficultySatisAvg3(difficultySatisAvg3)
			.difficultySatisAvg4(difficultySatisAvg4)
			.difficultySatisAvg5(difficultySatisAvg5)
			.build();
	}

	// 성향 분석 계산 메서드
	private double calculateTendencyScore(List<DiaryStat> diaryStats, Map<String, Double> weights) {
		double weightedSum = 0; // 가중 평균 점수 * 만족도 점수
		double weightSum = 0; // 만족도 점수의 합

		for (DiaryStat stat : diaryStats) {
			boolean allScoresValid = true; // 모든 항목이 존재 하는지 여부
			double causeScoreSum = 0; // 기준 항목 점수 * 가중치의 합

			for (Map.Entry<String, Double> entry : weights.entrySet()) {
				double score = (double)getScoreByKey(stat, entry.getKey());

				if (score <= 0) {
					allScoresValid = false;

					break; // 하나라도 값이 0인 경우 해당 기록은 제외
				}
				causeScoreSum += score * entry.getValue();
			}

			if (allScoresValid) {
				int satisfaction = stat.getSatisfaction();

				if (satisfaction > 0) {
					/**
					 * 기준 항목들의 가중 평균 점수"가 높았을 때, 그 테마에 대한 만족도가 얼마나 높았는가? 에 대한 계산
					 * 가중 평균 점수(원인 - 기준 항목들이 얼마나 부합했는지의 수치화) * 만족도(결과 - 그로 인해 얼마나 만족했는지) = 해당 기록의 성향 기여도
					 * 만족도 점수의 합 = 기준값
					 */
					weightedSum += causeScoreSum * satisfaction;
					weightSum += satisfaction;
				}
			}
		}

		return weightSum == 0 ? 0
			: Ut.calculator.roundToFirstDecimalAsDouble(weightedSum / weightSum);
	}

	private double getScoreByKey(DiaryStat stat, String key) {
		return switch (key) {
			case "fear" -> stat.getFear();
			case "production" -> stat.getProduction();
			case "difficulty" -> stat.getDifficulty();
			case "activity" -> stat.getActivity();
			case "story" -> stat.getStory();
			case "interior" -> stat.getInterior();
			case "question" -> stat.getQuestion();
			default -> 0;
		};
	}

	private double calculateAvgFromTuple(Map<Integer, Tuple> map, int level, int numeratorIdx, int denominatorIdx) {
		return Ut.calculator.roundToFirstDecimalAsDouble(
			Ut.calculator.calculateAverage(
				map.get(level).get(numeratorIdx, Long.class),
				map.get(level).get(denominatorIdx, Long.class)
			)
		);
	}
}
