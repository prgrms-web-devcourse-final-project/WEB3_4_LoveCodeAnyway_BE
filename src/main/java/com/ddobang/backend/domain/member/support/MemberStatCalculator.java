package com.ddobang.backend.domain.member.support;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.entity.DiaryStat;
import com.ddobang.backend.domain.diary.repository.DiaryStatRepository;
import com.ddobang.backend.domain.member.dto.stat.EscapeProfileStatDto;
import com.ddobang.backend.domain.member.dto.stat.EscapeScheduleStatDto;
import com.ddobang.backend.domain.member.dto.stat.EscapeSummaryStatDto;
import com.ddobang.backend.domain.member.entity.EscapeProfileStat;
import com.ddobang.backend.domain.member.entity.EscapeScheduleStat;
import com.ddobang.backend.domain.member.entity.EscapeSummaryStat;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberStat;
import com.ddobang.backend.domain.member.repository.MemberStatRepository;
import com.ddobang.backend.global.util.Ut;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStatCalculator {
	private final DiaryStatRepository diaryStatRepository;
	private final MemberStatRepository memberStatRepository;
	private static final DateTimeFormatter YM_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 M월");

	@Transactional
	public void updateMemberStatWithRetry(Member author) {
		int retryCount = 0;

		while (retryCount < 3) {
			try {
				updateMemberStat(author);

				return;
			} catch (ObjectOptimisticLockingFailureException e) {
				retryCount++;

				log.warn("OptimisticLock 충돌 발생, 재시도 중... ({}회)", retryCount);
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignored) {
				}
			}
		}
		log.error("최대 재시도 횟수 초과. author id = {} 업데이트 실패", author.getId());
	}

	public void updateMemberStat(Member author) {
		List<DiaryStat> diaryStats = diaryStatRepository.findByAuthorId(author.getId());
		EscapeSummaryStatDto escapeSummaryStatDto = calculateEscapeSummaryStat(diaryStats);
		EscapeProfileStatDto escapeProfileStatDto = calculateEscapeProfileStat(diaryStats, author.getId());
		EscapeScheduleStatDto escapeScheduleStatDto = calculateEscapeScheduleStat(diaryStats);
		Optional<MemberStat> memberStat = memberStatRepository.findById(author.getId());

		// 해당 멤버에 대한 일지가 없을 경우 분석 데이터 삭제
		if (diaryStats.isEmpty()) {
			memberStat.ifPresent(memberStatRepository::delete);
			return;
		}

		// 해당 멤버에 대한 분석 데이터가 없을 경우 분석 데이터 생성
		if (memberStat.isPresent()) {
			memberStat.get().update(
				escapeSummaryStatDto,
				escapeProfileStatDto,
				escapeScheduleStatDto
			);
		} else {
			EscapeSummaryStat escapeSummaryStat = EscapeSummaryStat.builder()
				.totalCount(escapeSummaryStatDto.totalCount())
				.successRate(escapeSummaryStatDto.successRate())
				.noHintSuccessCount(escapeSummaryStatDto.noHintSuccessCount())
				.noHintSuccessRate(escapeSummaryStatDto.noHintSuccessRate())
				.averageHintCount(escapeSummaryStatDto.averageHintCount())
				.firstEscapeDate(escapeSummaryStatDto.firstEscapeDate())
				.mostActiveMonth(escapeSummaryStatDto.mostActiveMonth())
				.mostActiveMonthCount(escapeSummaryStatDto.mostActiveMonthCount())
				.daysSinceFirstEscape(escapeSummaryStatDto.daysSinceFirstEscape())
				.build();

			EscapeProfileStat escapeProfileStat = EscapeProfileStat.builder()
				.tendencyStimulating(escapeProfileStatDto.tendencyStimulating())
				.tendencyLogical(escapeProfileStatDto.tendencyLogical())
				.tendencyNarrative(escapeProfileStatDto.tendencyNarrative())
				.tendencyActive(escapeProfileStatDto.tendencyActive())
				.tendencySpatial(escapeProfileStatDto.tendencySpatial())
				.genreCountMap(escapeProfileStatDto.genreCountMap())
				.genreSuccessMap(escapeProfileStatDto.genreSuccessMap())
				.difficultyHintAvg1(escapeProfileStatDto.difficultyHintAvg1())
				.difficultyHintAvg2(escapeProfileStatDto.difficultyHintAvg2())
				.difficultyHintAvg3(escapeProfileStatDto.difficultyHintAvg3())
				.difficultyHintAvg4(escapeProfileStatDto.difficultyHintAvg4())
				.difficultyHintAvg5(escapeProfileStatDto.difficultyHintAvg5())
				.difficultySatisAvg1(escapeProfileStatDto.difficultySatisAvg1())
				.difficultySatisAvg2(escapeProfileStatDto.difficultySatisAvg2())
				.difficultySatisAvg3(escapeProfileStatDto.difficultySatisAvg3())
				.difficultySatisAvg4(escapeProfileStatDto.difficultySatisAvg4())
				.difficultySatisAvg5(escapeProfileStatDto.difficultySatisAvg5())
				.build();

			EscapeScheduleStat escapeScheduleStat = EscapeScheduleStat.builder()
				.monthlyCountMap(escapeScheduleStatDto.monthlyCountMap())
				.lastMonthCount(escapeScheduleStatDto.lastMonthCount())
				.lastMonthAvgSatisfaction(escapeScheduleStatDto.lastMonthAvgSatisfaction())
				.lastMonthAvgHintCount(escapeScheduleStatDto.lastMonthAvgHintCount())
				.lastMonthSuccessRate(escapeScheduleStatDto.lastMonthSuccessRate())
				.lastMonthAvgTime(escapeScheduleStatDto.lastMonthAvgTime())
				.lastMonthTopTheme(escapeScheduleStatDto.lastMonthTopTheme())
				.lastMonthTopSatisfaction(escapeScheduleStatDto.lastMonthTopSatisfaction())
				.build();

			memberStatRepository.save(MemberStat.builder()
				.member(author)
				.escapeSummaryStat(escapeSummaryStat)
				.escapeProfileStat(escapeProfileStat)
				.escapeScheduleStat(escapeScheduleStat)
				.build());
		}
	}

	// 매달 1일 스케쥴링용 메서드
	void upDateEscapeScheduleStat(MemberStat memberStat) {
		List<DiaryStat> diaryStats = diaryStatRepository.findByAuthorId(memberStat.getId());

		EscapeScheduleStatDto escapeScheduleStatDto = calculateEscapeScheduleStat(diaryStats);
		memberStat.getEscapeScheduleStat().update(escapeScheduleStatDto);
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

			LocalDate date = stat.getEscapeDate();

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
				.max(Comparator
					.comparing(Map.Entry<YearMonth, Integer>::getValue) // value 기준 내림 차순
					.thenComparing(Map.Entry::getKey) // 날짜 기준 최신순
				)
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
					? mostActiveMonth.getKey().format(YM_FORMATTER) : null
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

	// EscapeScheduleStat 계산 메서드
	public EscapeScheduleStatDto calculateEscapeScheduleStat(List<DiaryStat> diaryStats) {
		Map<String, Integer> monthlyCountMap = new LinkedHashMap<>();
		YearMonth lastMonth = YearMonth.now().minusMonths(1);

		long lastMonthTotalSatis = 0;
		long lastMonthTotalTime = 0;
		int lastMonthTopSatisfaction = 0;
		long lastMonthTopSatisStatId = 0;
		LocalDate lastMonthTopThemeDate = null;

		int lastMonthCount = 0;
		long lastMonthSatisCount = 0;
		long lastMonthHintCount = 0;
		long lastMonthTimeCount = 0;
		long lastMonthTotalHintCount = 0;
		long lastMonthSuccessCount = 0;

		// 최근 6개월 초기화 (0으로)
		for (int i = 5; i >= 0; i--) {
			monthlyCountMap.put(YearMonth.now().minusMonths(i).format(YM_FORMATTER), 0);
		}

		for (DiaryStat stat : diaryStats) {
			LocalDate escapeDate = stat.getEscapeDate();

			if (escapeDate == null) {
				continue;
			}

			YearMonth escapeDateYM = YearMonth.from(escapeDate);
			String escapeDateYMStr = escapeDateYM.format(YM_FORMATTER);

			if (monthlyCountMap.containsKey(escapeDateYMStr)) {
				monthlyCountMap.put(escapeDateYMStr, monthlyCountMap.get(escapeDateYMStr) + 1);
			}

			if (escapeDateYM.equals(lastMonth)) {
				lastMonthCount++;

				if (stat.isEscapeResult()) {
					lastMonthSuccessCount++;
				}

				if (stat.getHintCount() != null) {
					lastMonthHintCount++;
					lastMonthTotalHintCount += stat.getHintCount();
				}

				if (stat.getSatisfaction() != 0) {
					lastMonthSatisCount++;
					lastMonthTotalSatis += stat.getSatisfaction();

					if (stat.getSatisfaction() > lastMonthTopSatisfaction) {
						lastMonthTopSatisfaction = stat.getSatisfaction();
						lastMonthTopSatisStatId = stat.getId();
						lastMonthTopThemeDate = stat.getEscapeDate();

						// 이번달 최고 만족도 테마가 중복될 경우 더 최근에 했던 테마로 저장
					} else if (stat.getSatisfaction() == lastMonthTopSatisfaction) {
						if (stat.getEscapeDate().isAfter(lastMonthTopThemeDate)) {
							lastMonthTopSatisStatId = stat.getId();
							lastMonthTopThemeDate = stat.getEscapeDate();
						}
					}
				}

				if (stat.getElapsedTime() != 0) {
					lastMonthTimeCount++;
					lastMonthTotalTime += stat.getElapsedTime();
				}
			}
		}

		double lastMonthAvgSatisfaction = Ut.calculator.roundToFirstDecimalAsDouble(
			Ut.calculator.calculateAverage(lastMonthTotalSatis, lastMonthSatisCount)
		);

		double lastMonthAvgHintCount = Ut.calculator.roundToFirstDecimalAsDouble(
			Ut.calculator.calculateAverage(lastMonthTotalHintCount, lastMonthHintCount)
		);

		double lastMonthSuccessRate = Ut.calculator.roundToFirstDecimalAsDouble(
			Ut.calculator.calculateRate(lastMonthCount, lastMonthSuccessCount)
		);

		int lastMonthAvgTime = Ut.calculator.roundToInt(
			Ut.calculator.calculateAverage(lastMonthTotalTime, lastMonthTimeCount)
		);

		String lastMonthTopTheme = lastMonthTopSatisStatId != 0
			? diaryStatRepository.findById(lastMonthTopSatisStatId)
			.orElseThrow().getTheme().getName() : null;

		return EscapeScheduleStatDto.builder()
			.monthlyCountMap(monthlyCountMap)
			.lastMonthCount(lastMonthCount)
			.lastMonthAvgSatisfaction(lastMonthAvgSatisfaction)
			.lastMonthAvgHintCount(lastMonthAvgHintCount)
			.lastMonthSuccessRate(lastMonthSuccessRate)
			.lastMonthAvgTime(lastMonthAvgTime)
			.lastMonthTopTheme(lastMonthTopTheme)
			.lastMonthTopSatisfaction(lastMonthTopSatisfaction)
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
				double score = getScoreByKey(stat, entry.getKey());

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
		if (map.get(level) == null) {
			return 0;
		}

		return Ut.calculator.roundToFirstDecimalAsDouble(
			Ut.calculator.calculateAverage(
				map.get(level).get(numeratorIdx, Integer.class),
				map.get(level).get(denominatorIdx, Long.class)
			)
		);
	}
}