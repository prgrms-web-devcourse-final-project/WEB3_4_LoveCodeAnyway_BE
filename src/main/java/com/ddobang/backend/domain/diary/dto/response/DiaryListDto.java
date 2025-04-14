package com.ddobang.backend.domain.diary.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.ddobang.backend.domain.diary.entity.Diary;

public record DiaryListDto(
	long id,
	long themeId,
	String themeName,
	String thumbnailUrl,
	List<String> tags,
	String storeName,
	LocalDate escapeDate,
	int elapsedTime,
	int hintCount,
	boolean escapeResult
) {
	public static DiaryListDto of(Diary diary) {
		return new DiaryListDto(
			diary.getId(),
			diary.getTheme().getId(),
			diary.getTheme().getName(),
			diary.getTheme().getThumbnailUrl(),
			diary.getTheme().getThemeTagMappings().stream()
				.map(ttm -> ttm.getThemeTag().getName())
				.toList(),
			diary.getTheme().getStore().getName(),
			diary.getDiaryStat().getEscapeDate(),
			diary.getDiaryStat().getElapsedTime(),
			diary.getDiaryStat().getHintCount(),
			diary.getDiaryStat().isEscapeResult()
		);
	}
}