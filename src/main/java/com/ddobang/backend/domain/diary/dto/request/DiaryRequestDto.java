package com.ddobang.backend.domain.diary.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DiaryRequestDto(
	@NotNull(message = "테마를 선택해주세요.")
	Long themeId,
	String imageUrl,
	LocalDate escapeDate,
	String participants,

	@Max(value = 5, message = "난이도는 최대 5 이하여야 합니다.")
	int difficulty,

	@Max(value = 5, message = "공포도는 최대 5 이하여야 합니다.")
	int fear,

	@Max(value = 5, message = "활동성은 최대 5 이하여야 합니다.")
	int activity,

	@Max(value = 5, message = "만족도는 최대 5 이하여야 합니다.")
	int satisfaction,

	@Max(value = 5, message = "연출은 최대 5 이하여야 합니다.")
	int production,

	@Max(value = 5, message = "스토리는 최대 5 이하여야 합니다.")
	int story,

	@Max(value = 5, message = "문제 구성은 최대 5 이하여야 합니다.")
	int question,

	@Max(value = 5, message = "인테리어는 최대 5 이하여야 합니다.")
	int interior,

	@Max(value = 100, message = "장치 비율은 최대 100% 이하여야 합니다.")
	int deviceRatio,

	int hintCount,
	boolean escapeResult,
	String timeType,
	int elapsedTime,
	String review
) {
}
