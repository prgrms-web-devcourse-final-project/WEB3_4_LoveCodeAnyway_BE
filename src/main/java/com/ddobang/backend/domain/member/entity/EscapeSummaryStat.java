package com.ddobang.backend.domain.member.entity;

import java.time.LocalDate;

import com.ddobang.backend.domain.member.dto.stat.EscapeSummaryStatDto;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class EscapeSummaryStat {
	// 통계 관련 필드
	private int totalCount; // 총 방탈출 횟수
	private double successRate; // 탈출 성공률
	private int noHintSuccessCount; // 노힌트 클리어 갯수
	private double noHintSuccessRate; // 노힌트 성공률
	private double averageHintCount; // 평균 힌트

	// 방탈출 여정
	private LocalDate firstEscapeDate; // 첫 탈출일
	private String mostActiveMonth; // 최대 탈출 월
	private int mostActiveMonthCount; // 최대 탈출한 달의 횟수
	private int daysSinceFirstEscape; // 방탈출을 시작한 날로부터 날짜수

	@Builder
	public EscapeSummaryStat(
		int totalCount,
		double successRate,
		int noHintSuccessCount,
		double noHintSuccessRate,
		double averageHintCount,
		LocalDate firstEscapeDate,
		String mostActiveMonth,
		int mostActiveMonthCount,
		int daysSinceFirstEscape
	) {
		this.totalCount = totalCount;
		this.successRate = successRate;
		this.noHintSuccessCount = noHintSuccessCount;
		this.noHintSuccessRate = noHintSuccessRate;
		this.averageHintCount = averageHintCount;
		this.firstEscapeDate = firstEscapeDate;
		this.mostActiveMonth = mostActiveMonth;
		this.mostActiveMonthCount = mostActiveMonthCount;
		this.daysSinceFirstEscape = daysSinceFirstEscape;
	}

	public void update(EscapeSummaryStatDto escapeSummaryStatDto) {
		this.totalCount = escapeSummaryStatDto.totalCount();
		this.successRate = escapeSummaryStatDto.successRate();
		this.noHintSuccessCount = escapeSummaryStatDto.noHintSuccessCount();
		this.noHintSuccessRate = escapeSummaryStatDto.noHintSuccessRate();
		this.averageHintCount = escapeSummaryStatDto.averageHintCount();
		this.firstEscapeDate = escapeSummaryStatDto.firstEscapeDate();
		this.mostActiveMonth = escapeSummaryStatDto.mostActiveMonth();
		this.mostActiveMonthCount = escapeSummaryStatDto.mostActiveMonthCount();
		this.daysSinceFirstEscape = escapeSummaryStatDto.daysSinceFirstEscape();
	}
}
