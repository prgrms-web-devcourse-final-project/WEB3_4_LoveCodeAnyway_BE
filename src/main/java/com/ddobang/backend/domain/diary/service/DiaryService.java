package com.ddobang.backend.domain.diary.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.dto.DiaryDto;
import com.ddobang.backend.domain.diary.dto.DiaryRequestDto;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.entity.DiaryStats;
import com.ddobang.backend.domain.diary.repository.DiaryRepository;
import com.ddobang.backend.domain.diary.repository.DiaryStatsRepository;
import com.ddobang.backend.domain.theme.repository.ThemeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {
	private final DiaryRepository diaryRepository;
	private final DiaryStatsRepository diaryStatsRepository;
	private final ThemeRepository themeRepository;

	@Transactional
	public DiaryDto write(DiaryRequestDto diaryRequestDto) {
		// Theme theme = themeRepository.findById(diaryRequestDto.theme_id()).orElseThrow(
		// 	() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND)
		// );

		Diary diary = diaryRepository.save(
			new Diary(
				//theme,
				diaryRequestDto.escapeDate(),
				diaryRequestDto.image(),
				diaryRequestDto.participants(),
				diaryRequestDto.review()
			)
		);

		DiaryStats diaryStats = diaryStatsRepository.save(
			new DiaryStats(
				diary,
				diaryRequestDto.difficulty(),
				diaryRequestDto.fear(),
				diaryRequestDto.activity(),
				diaryRequestDto.satisfaction(),
				diaryRequestDto.production(),
				diaryRequestDto.story(),
				diaryRequestDto.question(),
				diaryRequestDto.interior(),
				diaryRequestDto.deviceRatio(),
				diaryRequestDto.hintCount(),
				diaryRequestDto.escapeResult(),
				// diaryRequestDto.timeType.equals("remaining")
				// 	? theme.getRuntime() * 60 - diaryRequestDto.elapsedTime()
				// 	: diaryRequestDto.elapsedTime()
				diaryRequestDto.elapsedTime()
			)
		);

		diary.setDiaryStats(diaryStats);

		return new DiaryDto(diary);
	}

	public long count() {
		return diaryRepository.count();
	}
}
