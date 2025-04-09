package com.ddobang.backend.domain.diary.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record DiaryRequestDto(
	@NotNull(message = "테마를 선택해주세요.")
	Long themeId,
	LocalDate escapeDate,
	String participants,

	@PositiveOrZero
	@Max(value = 5, message = "난이도는 최대 5 이하여야 합니다.")
	int difficulty,

	@PositiveOrZero
	@Max(value = 5, message = "공포도는 최대 5 이하여야 합니다.")
	int fear,

	@PositiveOrZero
	@Max(value = 5, message = "활동성은 최대 5 이하여야 합니다.")
	int activity,

	@PositiveOrZero
	@Max(value = 5, message = "만족도는 최대 5 이하여야 합니다.")
	int satisfaction,

	@PositiveOrZero
	@Max(value = 5, message = "연출은 최대 5 이하여야 합니다.")
	int production,

	@PositiveOrZero
	@Max(value = 5, message = "스토리는 최대 5 이하여야 합니다.")
	int story,

	@PositiveOrZero
	@Max(value = 5, message = "문제 구성은 최대 5 이하여야 합니다.")
	int question,

	@PositiveOrZero
	@Max(value = 5, message = "인테리어는 최대 5 이하여야 합니다.")
	int interior,

	@PositiveOrZero
	@Max(value = 100, message = "장치 비율은 최대 100% 이하여야 합니다.")
	Integer deviceRatio,

	@PositiveOrZero
	Integer hintCount,

	boolean escapeResult,

	@NotBlank(message = "탈출 시간 타입을 선택해주세요.")
	String timeType,

	String elapsedTime,
	String review
) {
}
