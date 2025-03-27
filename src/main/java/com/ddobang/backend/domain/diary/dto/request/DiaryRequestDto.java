package com.ddobang.backend.domain.diary.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record DiaryRequestDto(
	@NotNull(message = "테마를 선택해주세요.")
	Long themeId,
	String image,
	LocalDate escapeDate,
	String participants,
	int difficulty,
	int fear,
	int activity,
	int satisfaction,
	int production,
	int story,
	int question,
	int interior,
	int deviceRatio,
	int hintCount,
	boolean escapeResult,
	String timeType,
	int elapsedTime,
	String review
) {
}
