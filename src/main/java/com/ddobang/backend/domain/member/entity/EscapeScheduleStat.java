package com.ddobang.backend.domain.member.entity;

import java.util.Map;

import com.ddobang.backend.domain.member.dto.stat.EscapeScheduleStatDto;
import com.ddobang.backend.global.converter.MapStrIntToJsonConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class EscapeScheduleStat {
	@Column(columnDefinition = "TEXT")
	@Convert(converter = MapStrIntToJsonConverter.class)
	private Map<String, Integer> monthlyCountMap; // 월별 방탈출 참여 횟수

	// 이번 달 기준 데이터
	private int thisMonthCount; // 이번달 참여 테마수
	private double thisMonthAvgSatisfaction; // 이번달 평균 힌트 갯수
	private double thisMonthAvgHintCount; // 최대 탈출 월
	private double thisMonthSuccessRate; // 이번달 탈출 성공률
	private int thisMonthAvgTime; // 이번달 평균 탈출 시간
	private String thisMonthTopTheme; // 이번달 최고 평가 테마
	private int thisMonthTopSatisfaction; // 이번달 최고 평가 테마 만족도

	@Builder
	public EscapeScheduleStat(
		Map<String, Integer> monthlyCountMap,
		int thisMonthCount,
		double thisMonthAvgSatisfaction,
		double thisMonthAvgHintCount,
		double thisMonthSuccessRate,
		int thisMonthAvgTime,
		String thisMonthTopTheme,
		int thisMonthTopSatisfaction
	) {
		this.monthlyCountMap = monthlyCountMap;
		this.thisMonthCount = thisMonthCount;
		this.thisMonthAvgSatisfaction = thisMonthAvgSatisfaction;
		this.thisMonthAvgHintCount = thisMonthAvgHintCount;
		this.thisMonthSuccessRate = thisMonthSuccessRate;
		this.thisMonthAvgTime = thisMonthAvgTime;
		this.thisMonthTopTheme = thisMonthTopTheme;
		this.thisMonthTopSatisfaction = thisMonthTopSatisfaction;
	}

	public void update(EscapeScheduleStatDto escapeScheduleStatDto) {
		this.monthlyCountMap = escapeScheduleStatDto.monthlyCountMap();
		this.thisMonthCount = escapeScheduleStatDto.thisMonthCount();
		this.thisMonthAvgSatisfaction = escapeScheduleStatDto.thisMonthAvgSatisfaction();
		this.thisMonthAvgHintCount = escapeScheduleStatDto.thisMonthAvgHintCount();
		this.thisMonthSuccessRate = escapeScheduleStatDto.thisMonthSuccessRate();
		this.thisMonthAvgTime = escapeScheduleStatDto.thisMonthAvgTime();
		this.thisMonthTopTheme = escapeScheduleStatDto.thisMonthTopTheme();
		this.thisMonthTopSatisfaction = escapeScheduleStatDto.thisMonthTopSatisfaction();
	}
}
