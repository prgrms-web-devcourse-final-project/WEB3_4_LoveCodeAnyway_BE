package com.ddobang.backend.domain.theme.support;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.entity.DiaryStat;
import com.ddobang.backend.domain.diary.repository.DiaryStatRepository;
import com.ddobang.backend.domain.theme.dto.ThemeStatDto;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
import com.ddobang.backend.global.util.Ut;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ThemeStatCalculator {
	private final DiaryStatRepository diaryStatRepository;
	private final ThemeStatRepository themeStatRepository;

	@Transactional
	public void updateThemeStat(Theme theme) {
		Long themeId = theme.getId();
		List<DiaryStat> diaryStats = diaryStatRepository.findByThemeId(themeId);
		ThemeStatDto stats = calculateThemeStat(diaryStats);
		Optional<ThemeStat> themeStat = themeStatRepository.findById(themeId);

		// 해당 테마에 대한 일지가 없을 경우 통계 삭제
		if (diaryStats.isEmpty()) {
			themeStat.ifPresent(themeStatRepository::delete);
			return;
		}

		// 해당 테마에 대한 통계가 없을 경우 통계 생성
		if (themeStat.isPresent()) {
			themeStat.get().updateStat(
				stats.difficulty(),
				stats.fear(),
				stats.activity(),
				stats.satisfaction(),
				stats.production(),
				stats.story(),
				stats.question(),
				stats.interior(),
				stats.deviceRatio(),
				stats.noHintEscapeRate(),
				stats.escapeResult(),
				stats.escapeTimeAvg(),
				diaryStats.size());
		} else {
			themeStatRepository.save(ThemeStat.builder()
				.theme(theme)
				.difficulty(stats.difficulty())
				.fear(stats.fear())
				.activity(stats.activity())
				.satisfaction(stats.satisfaction())
				.production(stats.production())
				.story(stats.story())
				.question(stats.question())
				.interior(stats.interior())
				.deviceRatio(stats.deviceRatio())
				.noHintEscapeRate(stats.noHintEscapeRate())
				.escapeResult(stats.escapeResult())
				.escapeTimeAvg(stats.escapeTimeAvg())
				.diaryCount(diaryStats.size())
				.build());
		}
	}

	// 테마 평가 및 통계 계산 메서드(힌트 갯수, 장치 비율 제외 0은 계산에서 제외합니다.)
	private ThemeStatDto calculateThemeStat(List<DiaryStat> diaryStats) {
		long totalCount = diaryStats.size();

		long totalDifficulty = 0;
		long totalFear = 0;
		long totalActivity = 0;
		long totalSatisfaction = 0;
		long totalProduction = 0;
		long totalStory = 0;
		long totalQuestion = 0;
		long totalInterior = 0;
		long totalDeviceRatio = 0;
		long totalElapsedTime = 0;

		long difficultyCount = 0;
		long fearCount = 0;
		long activityCount = 0;
		long satisfactionCount = 0;
		long productionCount = 0;
		long storyCount = 0;
		long questionCount = 0;
		long interiorCount = 0;
		long deviceRatioCount = 0;

		long escapeSuccessCount = 0;
		long noHintEscapeCount = 0;
		long elapsedTimeCount = 0;

		for (DiaryStat stat : diaryStats) {
			if (stat.getDifficulty() != 0) {
				totalDifficulty += stat.getDifficulty();
				difficultyCount++;
			}

			if (stat.getFear() != 0) {
				totalFear += stat.getFear();
				fearCount++;
			}

			if (stat.getActivity() != 0) {
				totalActivity += stat.getActivity();
				activityCount++;
			}

			if (stat.getSatisfaction() != 0) {
				totalSatisfaction += stat.getSatisfaction();
				satisfactionCount++;
			}

			if (stat.getProduction() != 0) {
				totalProduction += stat.getProduction();
				productionCount++;
			}

			if (stat.getStory() != 0) {
				totalStory += stat.getStory();
				storyCount++;
			}

			if (stat.getQuestion() != 0) {
				totalQuestion += stat.getQuestion();
				questionCount++;
			}

			if (stat.getInterior() != 0) {
				totalInterior += stat.getInterior();
				interiorCount++;
			}

			if (stat.getDeviceRatio() != null) {
				totalDeviceRatio += stat.getDeviceRatio();
				deviceRatioCount++;
			}

			if (stat.isEscapeResult()) {
				escapeSuccessCount++;
				if (stat.getHintCount() == 0) {
					noHintEscapeCount++;
				}
			}

			if (stat.getElapsedTime() != 0) {
				totalElapsedTime += stat.getElapsedTime();
				elapsedTimeCount++;
			}
		}

		float difficultyAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalDifficulty, difficultyCount)
		);

		float fearAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalFear, fearCount)
		);

		float activityAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalActivity, activityCount)
		);

		float satisfactionAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalSatisfaction, satisfactionCount)
		);

		float productionAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalProduction, productionCount)
		);

		float storyAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalStory, storyCount)
		);

		float questionAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalQuestion, questionCount)
		);

		float interiorAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalInterior, interiorCount)
		);

		float deviceRatioAvg = Ut.calculator.roundToFirstDecimal(
			Ut.calculator.calculateAverage(totalDeviceRatio, deviceRatioCount)
		);

		int noHintEscapeRate = Ut.calculator.roundToInt(
			Ut.calculator.calculateRate(totalCount, noHintEscapeCount)
		);

		int escapedRate = Ut.calculator.roundToInt(
			Ut.calculator.calculateRate(totalCount, escapeSuccessCount)
		);

		int escapeTimeAvg = Ut.calculator.roundToInt(
			Ut.calculator.calculateAverage(totalElapsedTime, elapsedTimeCount)
		);

		return ThemeStatDto.builder()
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
	}
}
