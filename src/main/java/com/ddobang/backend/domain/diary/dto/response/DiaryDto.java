package com.ddobang.backend.domain.diary.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ddobang.backend.domain.diary.entity.Diary;

public record DiaryDto(
	long id,
	long themeId,
	String themeName,
	String thumbnailUrl,
	String storeName,
	String imageUrl,
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
	int elapsedTime,
	String review,
	LocalDateTime createdAt,
	LocalDateTime modifiedAt
) {
	public static DiaryDto of(Diary diary) {
		return new DiaryDto(
			diary.getId(),
			diary.getTheme().getId(),
			diary.getTheme().getName(),
			diary.getTheme().getThumbnailUrl(),
			diary.getTheme().getStore().getName(),
			diary.getImageUrl(),
			diary.getDiaryStat().getEscapeDate(),
			diary.getParticipants(),
			diary.getDiaryStat().getDifficulty(),
			diary.getDiaryStat().getFear(),
			diary.getDiaryStat().getActivity(),
			diary.getDiaryStat().getSatisfaction(),
			diary.getDiaryStat().getProduction(),
			diary.getDiaryStat().getStory(),
			diary.getDiaryStat().getQuestion(),
			diary.getDiaryStat().getInterior(),
			diary.getDiaryStat().getDeviceRatio() != null
				? diary.getDiaryStat().getDeviceRatio() : 0,
			diary.getDiaryStat().getHintCount() != null
				? diary.getDiaryStat().getHintCount() : 0,
			diary.getDiaryStat().isEscapeResult(),
			diary.getDiaryStat().getElapsedTime(),
			diary.getReview(),
			diary.getCreatedAt(),
			diary.getModifiedAt()
		);
	}
}
