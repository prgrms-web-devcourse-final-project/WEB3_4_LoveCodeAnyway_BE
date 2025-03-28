package com.ddobang.backend.domain.diary.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.converter.DiaryConverter;
import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.dto.response.DiaryDto;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.entity.DiaryStats;
import com.ddobang.backend.domain.diary.exception.DiaryErrorCode;
import com.ddobang.backend.domain.diary.exception.DiaryException;
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
		// Theme theme = themeRepository.findById(diaryRequestDto.themeId()).orElseThrow(
		// 	() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND)
		// );

		Diary diary = diaryRepository.save(
			DiaryConverter.toDiary(diaryRequestDto)
		);

		DiaryStats diaryStats = diaryStatsRepository.save(
			DiaryConverter.toDiaryStats(diary, diaryRequestDto)
		);

		diary.setDiaryStats(diaryStats);

		return DiaryDto.of(diary);
	}

	public long count() {
		return diaryRepository.count();
	}

	@Transactional(readOnly = true)
	public Page<DiaryDto> getItemsAll(int page, int pageSize) {
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

		return diaryRepository.findAll(pageable)
			.map(DiaryDto::of);
	}

	public Diary findById(long id) {
		return diaryRepository.findById(id).orElseThrow(
			() -> new DiaryException(DiaryErrorCode.DIARY_NOT_FOUND)
		);
	}

	@Transactional(readOnly = true)
	public DiaryDto getItem(long id) {
		return DiaryDto.of(findById(id));
	}

	@Transactional
	public DiaryDto modify(long id, DiaryRequestDto diaryRequestDto) {
		Diary diary = findById(id);
		// Theme theme = themeRepository.findById(diaryRequestDto.themeId()).orElseThrow(
		// 	() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND)
		// );

		// int elapsedTime = diaryRequestDto.timeType().equals("remaining")
		// 	? theme.getRuntime() * 60 - diaryRequestDto.elapsedTime()
		// 	: diaryRequestDto.elapsedTime();
		int elapsedTime = diaryRequestDto.elapsedTime();

		DiaryConverter.updateDiary(diary, diaryRequestDto, elapsedTime);

		return DiaryDto.of(diary);
	}

	@Transactional
	public void delete(long id) {
		Diary diary = findById(id);

		diaryRepository.delete(diary);
	}
}
