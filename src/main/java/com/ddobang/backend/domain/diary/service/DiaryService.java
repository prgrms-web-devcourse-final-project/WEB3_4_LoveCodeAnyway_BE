package com.ddobang.backend.domain.diary.service;

import static com.ddobang.backend.domain.diary.entity.Diary.*;
import static com.ddobang.backend.domain.diary.entity.DiaryStat.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.ddobang.backend.domain.member.support.MemberStatCalculator;
import com.ddobang.backend.domain.theme.dto.request.ThemeForMemberRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.domain.theme.support.ThemeStatCalculator;
import com.ddobang.backend.global.security.LoginMemberProvider;
import com.ddobang.backend.domain.upload.event.DiaryImageChangedEvent;
import com.ddobang.backend.global.event.EventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {
	private final DiaryRepository diaryRepository;
	private final DiaryStatRepository diaryStatRepository;
	private final ThemeService themeService;
	private final ThemeStatCalculator themeStatCalculator;
	private final MemberStatCalculator memberStatCalculator;
	private final LoginMemberProvider loginMemberProvider;
	private final String TIME_MINUTES_SECONDS_PATTERN = "^\\d{1,3}:\\d{1,2}$";
	private final String TIME_TYPE_REMAINING = "REMAINING";
	private final String TIME_TYPE_ELAPSED = "ELAPSED";

	private final EventPublisher publisher;

	@Transactional
	public DiaryDto write(DiaryRequestDto diaryRequestDto) {
		Theme theme = themeService.getThemeById(diaryRequestDto.themeId());
		Member actor = loginMemberProvider.getCurrentMember();

		return DiaryDto.of(save(actor, diaryRequestDto));
	}

	public Diary save(Member author, DiaryRequestDto diaryRequestDto) {
		Theme theme = themeService.getThemeById(diaryRequestDto.themeId());

		if (diaryRepository.findByAuthorIdAndThemeId(author.getId(), diaryRequestDto.themeId()).isPresent()) {
			throw new DiaryException(DiaryErrorCode.DIARY_THEME_ALREADY_EXISTS);
		}

		int elapsedTime = calculateElapsedTime(
			diaryRequestDto.timeType(),
			theme.getRuntime(),
			diaryRequestDto.elapsedTime()
		);

		Diary diary = diaryRepository.save(
			toDiary(author, theme, diaryRequestDto)
		);

		DiaryStat diaryStat = diaryStatRepository.save(
			toDiaryStat(diary, diaryRequestDto, elapsedTime)
		);

		diary.setDiaryStat(diaryStat);
		themeStatCalculator.updateThemeStat(theme);
		memberStatCalculator.updateMemberStatWithRetry(author);

		return diary;
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
		Member actor = loginMemberProvider.getCurrentMember();
		Diary diary = findById(id);

		diary.checkActor(actor);

		return DiaryDto.of(diary);
	}

	@Transactional
	public DiaryDto modify(long id, DiaryRequestDto diaryRequestDto) {
		Diary diary = findById(id);
		Theme theme = themeService.getThemeById(diaryRequestDto.themeId());
		Member actor = loginMemberProvider.getCurrentMember();

		diary.checkActor(actor);

		int elapsedTime = calculateElapsedTime(
			diaryRequestDto.timeType(),
			theme.getRuntime(),
			diaryRequestDto.elapsedTime()
		);

		diary.modify(theme, diaryRequestDto);
		diary.getDiaryStat().modify(diaryRequestDto, elapsedTime);

		diaryRepository.flush();
		themeStatCalculator.updateThemeStat(theme);
		memberStatCalculator.updateMemberStatWithRetry(actor);

		return DiaryDto.of(diary);
	}

	@Transactional
	public void delete(long id) {
		Diary diary = findById(id);
		Theme theme = diary.getTheme();
		Member actor = loginMemberProvider.getCurrentMember();

		diary.checkActor(actor);
		String imageUrl = diary.getImageUrl();

		diaryRepository.delete(diary);
		publisher.publish(new DiaryImageChangedEvent(imageUrl));

		themeStatCalculator.updateThemeStat(theme);
		memberStatCalculator.updateMemberStatWithRetry(actor);
	}

	@Transactional(readOnly = true)
	public Page<DiaryListDto> getAllItems(DiaryFilterRequest request, int page, int pageSize) {
		if (request.isInvalidDateRange()) {
			throw new DiaryException(DiaryErrorCode.DIARY_INVALID_DATE_RANGE);
		}

		Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("id")));
		Member actor = loginMemberProvider.getCurrentMember();

		return diaryRepository.findDiariesByFilter(actor, request, pageable)
			.map(DiaryListDto::of);
	}

	@Transactional(readOnly = true)
	public List<DiaryListDto> getDiariesByMonth(int year, int month) {
		if (year == 0) {
			year = LocalDate.now().getYear();
		}

		if (month == 0) {
			month = LocalDate.now().getMonthValue();
		}

		if (year < 0 || month < 1 || month > 12) {
			throw new DiaryException(DiaryErrorCode.DIARY_INVALID_DATE);
		}

		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
		Member actor = loginMemberProvider.getCurrentMember();

		return diaryRepository.findByAuthorIdAndDiaryStat_EscapeDateBetween(actor.getId(), startDate, endDate)
			.stream()
			.map(DiaryListDto::of)
			.toList();
	}

	@Transactional
	public SimpleThemeResponse saveThemeForDiary(ThemeForMemberRequest request) {
		return themeService.saveForMember(request);
	}

	private int calculateElapsedTime(String timeType, int themeRuntime, String time) {
		if (time == null) {
			return 0;
		}

		if (!Pattern.matches(TIME_MINUTES_SECONDS_PATTERN, time)) {
			throw new DiaryException(DiaryErrorCode.DIARY_INVALID_TIME_FORMAT);
		}

		if (!TIME_TYPE_REMAINING.equals(timeType) && !TIME_TYPE_ELAPSED.equals(timeType)) {
			throw new DiaryException(DiaryErrorCode.DIARY_INVALID_TIME_TYPE);
		}

		String[] timeBits = time.split(":");
		int timeSeconds = Integer.parseInt(timeBits[0]) * 60 + Integer.parseInt(timeBits[1]);

		if (TIME_TYPE_REMAINING.equals(timeType) && themeRuntime * 60 < timeSeconds) {
			throw new DiaryException(DiaryErrorCode.DIARY_INVALID_REMAINING_TIME);
		}

		return TIME_TYPE_REMAINING.equals(timeType)
			? themeRuntime * 60 - timeSeconds
			: timeSeconds;
	}
}
