package com.ddobang.backend.domain.diary.converter;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.entity.DiaryStat;
import com.ddobang.backend.domain.theme.entity.Theme;

public class DiaryConverter {
	public static Diary toDiary(Theme theme, DiaryRequestDto dto) {
		return Diary.builder()
			.theme(theme)
			.escapeDate(dto.escapeDate())
			.imageUrl(dto.imageUrl())
			.participants(dto.participants())
			.review(dto.review())
			.build();
	}

	public static DiaryStat toDiaryStat(Diary diary, DiaryRequestDto dto, int elapsedTime) {
		return DiaryStat.builder()
			.diary(diary)
			.difficulty(dto.difficulty())
			.fear(dto.fear())
			.activity(dto.activity())
			.satisfaction(dto.satisfaction())
			.production(dto.production())
			.story(dto.story())
			.question(dto.question())
			.interior(dto.interior())
			.deviceRatio(dto.deviceRatio())
			.hintCount(dto.hintCount())
			.escapeResult(dto.escapeResult())
			.elapsedTime(elapsedTime)
			.build();
	}

	public static void updateDiary(
		Theme theme,
		Diary diary,
		DiaryRequestDto dto,
		int elapsedTime
	) {
		diary.modify(theme, dto);
		diary.getDiaryStats().modify(dto, elapsedTime);
	}
}
