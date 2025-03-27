package com.ddobang.backend.domain.diary.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional(readOnly = true)
	public Page<DiaryDto> getItemsAll(int page, int pageSize) {
		Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

		return diaryRepository.findAll(pageable)
			.map(DiaryDto::new);
	}

	public Diary findById(long id) {
		return diaryRepository.findById(id).orElseThrow(
			() -> new DiaryException(DiaryErrorCode.DIARY_NOT_FOUND)
		);
	}

	@Transactional(readOnly = true)
	public DiaryDto getItem(long id) {
		return new DiaryDto(findById(id));
	}
}
