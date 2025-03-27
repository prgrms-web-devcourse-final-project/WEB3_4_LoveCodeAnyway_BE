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
			Diary.builder()
				//.theme(theme)
				.escapeDate(diaryRequestDto.escapeDate())
				.imageUrl(diaryRequestDto.image())
				.participants(diaryRequestDto.participants())
				.review(diaryRequestDto.review())
				.build()
		);

		DiaryStats diaryStats = diaryStatsRepository.save(
			DiaryStats.builder()
				.diary(diary)
				.difficulty(diaryRequestDto.difficulty())
				.fear(diaryRequestDto.fear())
				.activity(diaryRequestDto.activity())
				.satisfaction(diaryRequestDto.satisfaction())
				.production(diaryRequestDto.production())
				.story(diaryRequestDto.story())
				.question(diaryRequestDto.question())
				.interior(diaryRequestDto.interior())
				.deviceRatio(diaryRequestDto.deviceRatio())
				.hintCount(diaryRequestDto.hintCount())
				.escapeResult(diaryRequestDto.escapeResult())
				// .elapsedTime(
				// 	diaryRequestDto.timeType().equals("remaining")
				// 		? theme.getRuntime() * 60 - diaryRequestDto.elapsedTime()
				// 		: diaryRequestDto.elapsedTime()
				// )
				.elapsedTime(diaryRequestDto.elapsedTime())
				.build()
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

		diary.modify(
			//theme,
			diaryRequestDto.escapeDate(),
			diaryRequestDto.image(),
			diaryRequestDto.participants(),
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
			// diaryRequestDto.timeType().equals("remaining")
			// 	? theme.getRuntime() * 60 - diaryRequestDto.elapsedTime()
			// 	: diaryRequestDto.elapsedTime(),
			diaryRequestDto.elapsedTime(),
			diaryRequestDto.review()
		);

		return DiaryDto.of(diary);
	}

	@Transactional
	public void delete(long id) {
		Diary diary = findById(id);

		diaryRepository.delete(diary);
	}
}
