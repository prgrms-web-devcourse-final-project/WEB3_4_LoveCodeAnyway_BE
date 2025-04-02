package com.ddobang.backend.domain.diary.service;

import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.converter.DiaryConverter;
import com.ddobang.backend.domain.diary.dto.request.DiaryFilterRequest;
import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.dto.response.DiaryDto;
import com.ddobang.backend.domain.diary.dto.response.DiaryListDto;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.diary.entity.DiaryStat;
import com.ddobang.backend.domain.diary.exception.DiaryErrorCode;
import com.ddobang.backend.domain.diary.exception.DiaryException;
import com.ddobang.backend.domain.diary.repository.DiaryRepository;
import com.ddobang.backend.domain.diary.repository.DiaryStatRepository;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.exception.ThemeErrorCode;
import com.ddobang.backend.domain.theme.exception.ThemeException;
import com.ddobang.backend.domain.theme.repository.ThemeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {
	private final DiaryRepository diaryRepository;
	private final DiaryStatRepository diaryStatRepository;
	private final ThemeRepository themeRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public DiaryDto write(DiaryRequestDto diaryRequestDto) {
		Theme theme = themeRepository.findById(diaryRequestDto.themeId()).orElseThrow(
			() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND)
		);
		Member actor = memberRepository.findById(1L).get();

		int elapsedTime = calculateElapsedTime(
			diaryRequestDto.timeType(),
			theme.getRuntime(),
			diaryRequestDto.elapsedTime()
		);

		Diary diary = diaryRepository.save(
			DiaryConverter.toDiary(actor, theme, diaryRequestDto)
		);

		DiaryStat diaryStat = diaryStatRepository.save(
			DiaryConverter.toDiaryStat(diary, diaryRequestDto, elapsedTime)
		);

		diary.setDiaryStat(diaryStat);

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
		Theme theme = themeRepository.findById(diaryRequestDto.themeId()).orElseThrow(
			() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND)
		);

		int elapsedTime = calculateElapsedTime(
			diaryRequestDto.timeType(),
			theme.getRuntime(),
			diaryRequestDto.elapsedTime()
		);

		DiaryConverter.updateDiary(theme, diary, diaryRequestDto, elapsedTime);

		return DiaryDto.of(diary);
	}

	@Transactional
	public void delete(long id) {
		Diary diary = findById(id);

		diaryRepository.delete(diary);
	}

	private int calculateElapsedTime(String timeType, int themeRuntime, String time) {
		if (time == null) {
			return 0;
		}

		if (!Pattern.matches("^\\d{1,3}:\\d{1,2}$", time)) {
			throw new DiaryException(DiaryErrorCode.DIARY_INVALID_TIME_FORMAT);
		}

		if (!timeType.equals("remaining") && !timeType.equals("elapsed")) {
			throw new DiaryException(DiaryErrorCode.DIARY_INVALID_TIME_TYPE);
		}

		String[] timeBits = time.split(":");
		int timeSeconds = Integer.parseInt(timeBits[0]) * 60 + Integer.parseInt(timeBits[1]);

		return timeType.equals("remaining")
			? themeRuntime * 60 - timeSeconds
			: timeSeconds;
	}

	@Transactional(readOnly = true)
	public Page<DiaryListDto> getAllItems(DiaryFilterRequest request, int page, int pageSize) {
		if (request.startDate() != null
			&& request.endDate() != null
			&& request.startDate().isAfter(request.endDate())) {
			throw new DiaryException(DiaryErrorCode.DIARY_INVALID_DATE_RANGE);
		}

		Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("id")));
		Member actor = memberRepository.findById(1L).get();

		return diaryRepository.findDiariesByFilter(actor, request, pageable)
			.map(DiaryListDto::of);
	}
}
