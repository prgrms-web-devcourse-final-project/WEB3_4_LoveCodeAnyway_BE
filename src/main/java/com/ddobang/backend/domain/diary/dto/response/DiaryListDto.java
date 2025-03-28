package com.ddobang.backend.domain.diary.dto.response;

import java.time.LocalDate;

import com.ddobang.backend.domain.diary.entity.Diary;

public record DiaryListDto(
	long id,
	//String themeName,
	//String thumbnail,
	//String storeName,
	LocalDate escapeDate,
	int hintCount,
	boolean escapeResult,
	int elapsedTime
) {
	public DiaryListDto(Diary diary) {
		this(
			diary.getId(),
			//diary.getTheme().getId(),
			//diary.getTheme().getThumbnail,
			//diary.getTheme().getStore().getName(),
			diary.getEscapeDate(),
			diary.getDiaryStats().getHintCount(),
			diary.getDiaryStats().isEscapeResult(),
			diary.getDiaryStats().getElapsedTime()
		);
	}
}
