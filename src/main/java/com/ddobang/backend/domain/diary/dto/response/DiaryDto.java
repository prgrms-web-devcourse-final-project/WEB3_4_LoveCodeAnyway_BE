package com.ddobang.backend.domain.diary.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ddobang.backend.domain.diary.entity.Diary;

public record DiaryDto(
	long id,
	//String themeName,
	//String thumbnail,
	//String storeName,
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
	int elapsedTime,
	String review,
	LocalDateTime createdAt,
	LocalDateTime modifiedAt
) {
	public static DiaryDto from(Diary diary) {
		return new DiaryDto(
			diary.getId(),
			//diary.getTheme().getId(),
			//diary.getTheme().getThumbnail,
			//diary.getTheme().getStore().getName(),
			diary.getImageUrl(),
			diary.getEscapeDate(),
			diary.getParticipants(),
			diary.getDiaryStats().getDifficulty(),
			diary.getDiaryStats().getFear(),
			diary.getDiaryStats().getActivity(),
			diary.getDiaryStats().getSatisfaction(),
			diary.getDiaryStats().getProduction(),
			diary.getDiaryStats().getStory(),
			diary.getDiaryStats().getQuestion(),
			diary.getDiaryStats().getInterior(),
			diary.getDiaryStats().getDeviceRatio(),
			diary.getDiaryStats().getHintCount(),
			diary.getDiaryStats().isEscapeResult(),
			diary.getDiaryStats().getElapsedTime(),
			diary.getReview(),
			diary.getCreatedAt(),
			diary.getModifiedAt()
		);
	}
}
