package com.ddobang.backend.domain.diary.application;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ddobang.backend.domain.diary.entity.DiaryStat;
import com.ddobang.backend.domain.diary.repository.DiaryStatRepository;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DiaryStatCalculator {
	private final DiaryStatRepository diaryStatRepository;
	private final ThemeStatRepository themeStatRepository;

	public void updateThemeStat(Theme theme) {
		List<DiaryStat> diaryStats = diaryStatRepository.findByThemeId(theme.getId());

		float difficultyAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getDifficulty)
			)
		);

		float fearAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getFear)
			)
		);

		float activityAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getActivity)
			)
		);

		float satisfactionAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getSatisfaction)
			)
		);

		float productionAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getProduction)
			)
		);

		float storyAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getStory)
			)
		);

		float questionAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getQuestion)
			)
		);

		float interiorAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getInterior)
			)
		);

		float deviceRatioAvg = roundToFirstDecimal(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getDeviceRatio)
			)
		);

		// 전체 시도 수
		long totalStats = diaryStats.size();

		// 성공한 갯수
		long totalEscaped = diaryStats.stream()
			.filter(DiaryStat::isEscapeResult)
			.count();

		// 힌트 없이 성공한 횟수
		long noHintEscaped = diaryStats.stream()
			.filter(stat -> stat.isEscapeResult() && stat.getHintCount() == 0)
			.count();

		int noHintEscapeRate = totalStats == 0 ? 0
			: (int)Math.round((double)noHintEscaped / totalStats * 100);

		int escapedRate = totalStats == 0 ? 0
			: (int)Math.round((double)totalEscaped / totalStats * 100);

		int escapeTimeAvg = roundToInt(
			calculateAverage(
				diaryStats.stream()
					.mapToInt(DiaryStat::getElapsedTime)
			)
		);

		ThemeStat themeStat = ThemeStat.builder()
			.theme(theme)
			.difficulty(difficultyAvg)
			.fear(fearAvg)
			.activity(activityAvg)
			.satisfaction(satisfactionAvg)
			.production(productionAvg)
			.story(storyAvg)
			.question(questionAvg)
			.interior(interiorAvg)
			.deviceRatio(deviceRatioAvg)
			.noHintEscapeRate(noHintEscapeRate)
			.escapeResult(escapedRate)
			.escapeTimeAvg(escapeTimeAvg)
			.build();

		themeStatRepository.save(themeStat);
	}

	public static float roundToFirstDecimal(double value) {
		return (float)Math.round(value * 10) / 10f;
	}

	public static int roundToInt(double value) {
		return (int)Math.round(value);
	}

	private double calculateAverage(IntStream stream) {
		return stream
			.filter(stat -> stat != 0)
			.average()
			.orElse(0.0);
	}
}
