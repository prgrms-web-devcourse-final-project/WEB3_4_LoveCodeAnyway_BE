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
import com.ddobang.backend.domain.member.dto.stat.EscapeSummaryStatDto;
import com.ddobang.backend.global.util.Ut;

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

		double noHintSuccessRate = Ut.calculator.roundToInt(
			Ut.calculator.calculateRate(totalCount, noHintSuccessCount)
		);

		double averageHintCount = Ut.calculator.roundToInt(
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
}
