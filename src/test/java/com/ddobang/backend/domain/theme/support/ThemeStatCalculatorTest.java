package com.ddobang.backend.domain.theme.support;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ddobang.backend.domain.diary.entity.DiaryStat;
import com.ddobang.backend.domain.diary.repository.DiaryStatRepository;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;

@ExtendWith(MockitoExtension.class)
public class ThemeStatCalculatorTest {
	@Mock
	private DiaryStatRepository diaryStatRepository;

	@Mock
	private ThemeStatRepository themeStatRepository;

	@InjectMocks
	private ThemeStatCalculator themeStatCalculator;

	@Test
	@DisplayName("테마 통계, 평가 정보 저장 테스트")
	void t1() {
		// given
		Theme theme = Theme.builder().build();
		ReflectionTestUtils.setField(theme, "id", 1L);

		List<DiaryStat> diaryStats = List.of(
			DiaryStat.builder()
				.difficulty(3)
				.fear(4)
				.activity(5)
				.satisfaction(0)
				.production(4)
				.story(3)
				.question(5)
				.interior(1)
				.deviceRatio(70)
				.hintCount(0)
				.escapeResult(false)
				.elapsedTime(3600)
				.build(),
			DiaryStat.builder()
				.difficulty(4)
				.fear(3)
				.activity(4)
				.satisfaction(3)
				.production(4)
				.story(4)
				.question(4)
				.interior(3)
				.deviceRatio(60)
				.hintCount(1)
				.escapeResult(true)
				.elapsedTime(2675)
				.build()
		);

		when(diaryStatRepository.findByThemeId(1L)).thenReturn(diaryStats);

		// when
		themeStatCalculator.updateThemeStat(theme);

		// then
		ArgumentCaptor<ThemeStat> captor = ArgumentCaptor.forClass(ThemeStat.class);
		verify(themeStatRepository).save(captor.capture());
		ThemeStat saved = captor.getValue();

		assertThat(saved.getTheme()).isEqualTo(theme);
		assertThat(saved.getDifficulty()).isEqualTo(3.5f); // 평균(3, 4)
		assertThat(saved.getFear()).isEqualTo(3.5f); // 평균(4, 3)
		assertThat(saved.getActivity()).isEqualTo(4.5f); // 평균(5, 4)
		assertThat(saved.getSatisfaction()).isEqualTo(3.0f);  // 평균(0, 3)
		assertThat(saved.getProduction()).isEqualTo(4.0f); // 평균(4, 4)
		assertThat(saved.getStory()).isEqualTo(3.5f); // 평균(3, 4)
		assertThat(saved.getQuestion()).isEqualTo(4.5f); // 평균(5, 4)
		assertThat(saved.getInterior()).isEqualTo(2.0f); // 평균(1, 3)
		assertThat(saved.getDeviceRatio()).isEqualTo(65.0f); // 평균(70, 60)
		assertThat(saved.getNoHintEscapeRate()).isEqualTo(0); // 1, 0
		assertThat(saved.getEscapeResult()).isEqualTo(50); // false, true
		assertThat(saved.getEscapeTimeAvg()).isEqualTo(3138); // 평균(3600, 2675)
	}
}
