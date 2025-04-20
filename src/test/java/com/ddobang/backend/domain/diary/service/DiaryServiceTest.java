package com.ddobang.backend.domain.diary.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
import com.ddobang.backend.global.security.CustomUserDetails;

/**
 * DiaryServiceTest
 * 일지 서비스 리포지토리 통합 테스트
 * @author 100minha
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DiaryServiceTest {

	@Autowired
	private DiaryService diaryService;
	@Autowired
	private ThemeStatRepository themeStatRepository;

	int diaryCount;

	@BeforeEach
	void setUp() {
		CustomUserDetails userDetails = new CustomUserDetails(1L, "nickname", false);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		diaryCount = themeStatRepository.findById(1L).get().getDiaryCount();
	}

	@Test
	@DisplayName("일지 작성 시 새 테마 통계 생성 테스트")
	void t1() {
		// given
		Long themeId = 10L;

		DiaryRequestDto diaryRequestDto = DiaryRequestDto.builder()
			.themeId(themeId)
			.escapeDate(LocalDate.of(2024, 1, 15))
			.participants("지인1, 지인2")
			.difficulty(1)
			.fear(1)
			.activity(1)
			.satisfaction(1)
			.production(1)
			.story(1)
			.question(1)
			.interior(1)
			.deviceRatio(50)
			.hintCount(1)
			.escapeResult(true)
			.timeType("REMAINING")
			.elapsedTime("15:25")
			.review("너무 재밌었다!!")
			.build();

		// when
		boolean isExist = themeStatRepository.existsById(themeId);
		diaryService.write(diaryRequestDto);
		ThemeStat updatedThemeStat = themeStatRepository.findById(themeId).get();

		// then
		assertThat(isExist).isFalse();
		assertThat(updatedThemeStat.getDiaryCount()).isEqualTo(1);
		assertThat(updatedThemeStat.getDeviceRatio()).isEqualTo((float)diaryRequestDto.deviceRatio());
		assertThat(updatedThemeStat.getEscapeResult()).isEqualTo(100);
		assertThat(updatedThemeStat.getSatisfaction()).isEqualTo((float)diaryRequestDto.satisfaction());
	}

	@Test
	@DisplayName("일지 수정 시 테마 통계 업데이트(더티체킹) 테스트")
	void t2() {
		// given
		Long themeId = 1L;

		// when
		diaryService.modify(
			1L,
			DiaryRequestDto.builder()
				.themeId(themeId)
				.escapeDate(LocalDate.of(2024, 1, 15))
				.participants("지인1, 지인2")
				.difficulty(1)
				.fear(1)
				.activity(1)
				.satisfaction(1)
				.production(1)
				.story(1)
				.question(1)
				.interior(1)
				.deviceRatio(50)
				.hintCount(1)
				.escapeResult(true)
				.timeType("REMAINING")
				.elapsedTime("15:25")
				.review("너무 재밌었다!!")
				.build()
		);
		ThemeStat updatedThemeStat = themeStatRepository.findById(themeId).get();

		// then
		assertThat(updatedThemeStat.getDiaryCount()).isEqualTo(1);
		assertThat(updatedThemeStat.getEscapeResult()).isEqualTo(100);
		assertThat(updatedThemeStat.getSatisfaction()).isEqualTo(1);
	}

	@Test
	@DisplayName("일지 삭제 시 테마 통계 업데이트(더티체킹) 테스트")
	void t3() {
		// given
		Long id = 1L;

		// when
		diaryService.delete(id);
		Optional<ThemeStat> updatedThemeStat = themeStatRepository.findById(id);

		// then
		assertThat(updatedThemeStat.isPresent()).isFalse();
	}
}
