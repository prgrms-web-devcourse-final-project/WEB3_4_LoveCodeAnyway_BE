package com.ddobang.backend.domain.diary.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record DiaryRequestDto(
	@NotBlank
	long theme_id,
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
