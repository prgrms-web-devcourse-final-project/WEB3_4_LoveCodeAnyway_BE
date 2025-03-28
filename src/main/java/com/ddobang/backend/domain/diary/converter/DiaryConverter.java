package com.ddobang.backend.domain.diary.converter;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.entity.DiaryStats;

public class DiaryConverter {
	public static Diary toDiary(DiaryRequestDto dto) {
		return Diary.builder()
			.escapeDate(dto.escapeDate())
			.imageUrl(dto.imageUrl())
			.participants(dto.participants())
			.review(dto.review())
			.build();
	}

	public static DiaryStats toDiaryStats(Diary diary, DiaryRequestDto dto) {
		return DiaryStats.builder()
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
			.elapsedTime(dto.elapsedTime())
			.build();
	}

	public static void updateDiary(Diary diary, DiaryRequestDto dto, int elapsedTime) {
		diary.modify(dto);
		diary.getDiaryStats().modify(dto, elapsedTime);
	}
}
