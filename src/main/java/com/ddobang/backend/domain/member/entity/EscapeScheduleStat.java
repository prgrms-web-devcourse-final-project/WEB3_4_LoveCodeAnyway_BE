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
	private int lastMonthCount; // 이번달 참여 테마수
	private double lastMonthAvgSatisfaction; // 이번달 평균 힌트 갯수
	private double lastMonthAvgHintCount; // 최대 탈출 월
	private double lastMonthSuccessRate; // 이번달 탈출 성공률
	private int lastMonthAvgTime; // 이번달 평균 탈출 시간
	private String lastMonthTopTheme; // 이번달 최고 평가 테마
	private int lastMonthTopSatisfaction; // 이번달 최고 평가 테마 만족도

	@Builder
	public EscapeScheduleStat(
		Map<String, Integer> monthlyCountMap,
		int lastMonthCount,
		double lastMonthAvgSatisfaction,
		double lastMonthAvgHintCount,
		double lastMonthSuccessRate,
		int lastMonthAvgTime,
		String lastMonthTopTheme,
		int lastMonthTopSatisfaction
	) {
		this.monthlyCountMap = monthlyCountMap;
		this.lastMonthCount = lastMonthCount;
		this.lastMonthAvgSatisfaction = lastMonthAvgSatisfaction;
		this.lastMonthAvgHintCount = lastMonthAvgHintCount;
		this.lastMonthSuccessRate = lastMonthSuccessRate;
		this.lastMonthAvgTime = lastMonthAvgTime;
		this.lastMonthTopTheme = lastMonthTopTheme;
		this.lastMonthTopSatisfaction = lastMonthTopSatisfaction;
	}

	public void update(EscapeScheduleStatDto escapeScheduleStatDto) {
		this.monthlyCountMap = escapeScheduleStatDto.monthlyCountMap();
		this.lastMonthCount = escapeScheduleStatDto.lastMonthCount();
		this.lastMonthAvgSatisfaction = escapeScheduleStatDto.lastMonthAvgSatisfaction();
		this.lastMonthAvgHintCount = escapeScheduleStatDto.lastMonthAvgHintCount();
		this.lastMonthSuccessRate = escapeScheduleStatDto.lastMonthSuccessRate();
		this.lastMonthAvgTime = escapeScheduleStatDto.lastMonthAvgTime();
		this.lastMonthTopTheme = escapeScheduleStatDto.lastMonthTopTheme();
		this.lastMonthTopSatisfaction = escapeScheduleStatDto.lastMonthTopSatisfaction();
	}
}
