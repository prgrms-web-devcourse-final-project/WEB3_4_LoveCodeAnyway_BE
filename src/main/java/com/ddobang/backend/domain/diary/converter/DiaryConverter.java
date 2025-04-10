package com.ddobang.backend.domain.diary.converter;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.entity.DiaryStat;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.theme.entity.Theme;

public class DiaryConverter {
	public static Diary toDiary(Member author, Theme theme, DiaryRequestDto dto) {
		return Diary.builder()
			.theme(theme)
			.author(author)
			.escapeDate(dto.escapeDate())
			.participants(dto.participants())
			.review(dto.review())
			.build();
	}

	public static DiaryStat toDiaryStat(Diary diary, DiaryRequestDto dto, int elapsedTime) {
		return DiaryStat.builder()
			.diary(diary)
			.theme(diary.getTheme())
			.author(diary.getAuthor())
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

	public static void modifyDiary(
		Theme theme,
		Diary diary,
		DiaryRequestDto dto,
		int elapsedTime
	) {
		diary.modify(theme, dto);
		diary.getDiaryStat().modify(dto, elapsedTime);
	}
}
