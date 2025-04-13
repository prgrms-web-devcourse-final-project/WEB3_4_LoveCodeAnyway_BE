package com.ddobang.backend.domain.member.support;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
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
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberStat;
import com.ddobang.backend.domain.member.repository.MemberStatRepository;
import com.ddobang.backend.domain.theme.entity.Theme;

@ExtendWith(MockitoExtension.class)
public class MemberStatCalculatorTest {
	@Mock
	private DiaryStatRepository diaryStatRepository;

	@Mock
	private MemberStatRepository memberStatRepository;

	@InjectMocks
	private MemberStatCalculator memberStatCalculator;

	private Theme theme1;
	private Theme theme2;
	private Theme theme3;

	private Member member;
	private List<DiaryStat> diaryStats;

	@BeforeEach
	void setUp() {
		theme1 = Theme.builder().build();
		ReflectionTestUtils.setField(theme1, "id", 1L);

		theme2 = Theme.builder().build();
		ReflectionTestUtils.setField(theme2, "id", 2L);

		theme3 = Theme.builder().build();
		ReflectionTestUtils.setField(theme3, "id", 3L);

		member = Member.builder().build();
		ReflectionTestUtils.setField(member, "id", 1L);

		diaryStats = List.of(
			DiaryStat.builder()
				.theme(theme1)
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
				.escapeDate(LocalDate.of(2025, 3, 5))
				.build(),
			DiaryStat.builder()
				.theme(theme2)
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
				.build(),
			DiaryStat.builder()
				.theme(theme3)
				.hintCount(0)
				.escapeResult(true)
				.escapeDate(LocalDate.of(2025, 2, 25))
				.build()
		);
	}

	@Test
	@DisplayName("사용자 분석, 통계 저장 테스트 - EscapeSummaryStat")
	void t1() {
		// given
		when(diaryStatRepository.findByAuthorId(1L)).thenReturn(diaryStats);

		// when
		memberStatCalculator.updateMemberStatWithRetry(member);

		// then
		ArgumentCaptor<MemberStat> captor = ArgumentCaptor.forClass(MemberStat.class);
		verify(memberStatRepository).save(captor.capture());
		MemberStat saved = captor.getValue();

		assertThat(saved.getEscapeSummaryStat().getTotalCount()).isEqualTo(3);
		assertThat(saved.getEscapeSummaryStat().getSuccessRate()).isEqualTo(66.7);
		assertThat(saved.getEscapeSummaryStat().getNoHintSuccessCount()).isEqualTo(1);
		assertThat(saved.getEscapeSummaryStat().getNoHintSuccessRate()).isEqualTo(33.3);
		assertThat(saved.getEscapeSummaryStat().getAverageHintCount()).isEqualTo(0.3);
		assertThat(saved.getEscapeSummaryStat().getFirstEscapeDate()).isEqualTo("2025-02-25");
		assertThat(saved.getEscapeSummaryStat().getMostActiveMonth()).isEqualTo("2025년 3월");
		assertThat(saved.getEscapeSummaryStat().getMostActiveMonthCount()).isEqualTo(1);
		assertThat(saved.getEscapeSummaryStat().getDaysSinceFirstEscape())
			.isEqualTo((int)ChronoUnit.DAYS.between(LocalDate.of(2025, 2, 25), LocalDate.now()));
	}

	@Test
	@DisplayName("사용자 분석, 통계 저장 테스트 - EscapeProfileStat")
	void t2() {
		// given
		when(diaryStatRepository.findByAuthorId(1L)).thenReturn(diaryStats);

		// when
		memberStatCalculator.updateMemberStatWithRetry(member);

		// then
		ArgumentCaptor<MemberStat> captor = ArgumentCaptor.forClass(MemberStat.class);
		verify(memberStatRepository).save(captor.capture());
		MemberStat saved = captor.getValue();

		assertThat(saved.getEscapeProfileStat().getTendencyStimulating()).isEqualTo(3.4);
		assertThat(saved.getEscapeProfileStat().getTendencyLogical()).isEqualTo(4);
		assertThat(saved.getEscapeProfileStat().getTendencyNarrative()).isEqualTo(4);
		assertThat(saved.getEscapeProfileStat().getTendencyActive()).isEqualTo(4);
		assertThat(saved.getEscapeProfileStat().getTendencySpatial()).isEqualTo(3.5);
		// 이 외에는 QueryDSL이기 때문에 Mocking 없어서 테스트 불가
	}

	@Test
	@DisplayName("사용자 분석, 통계 저장 테스트 - EscapeScheduleStat")
	void t3() {
		// given
		when(diaryStatRepository.findByAuthorId(1L)).thenReturn(diaryStats);

		// when
		memberStatCalculator.updateMemberStatWithRetry(member);

		// then
		ArgumentCaptor<MemberStat> captor = ArgumentCaptor.forClass(MemberStat.class);
		verify(memberStatRepository).save(captor.capture());
		MemberStat saved = captor.getValue();

		assertThat(saved.getEscapeScheduleStat().getMonthlyCountMap()).isEqualTo(
			Map.of("2025년 4월", 0, "2025년 3월", 1, "2025년 2월", 1, "2025년 1월", 0, "2024년 12월", 0, "2024년 11월", 0)
		);
		assertThat(saved.getEscapeScheduleStat().getLastMonthCount()).isEqualTo(1);
		assertThat(saved.getEscapeScheduleStat().getLastMonthAvgSatisfaction()).isEqualTo(0);
		assertThat(saved.getEscapeScheduleStat().getLastMonthAvgHintCount()).isEqualTo(0);
		assertThat(saved.getEscapeScheduleStat().getLastMonthSuccessRate()).isEqualTo(0);
		assertThat(saved.getEscapeScheduleStat().getLastMonthAvgTime()).isEqualTo(3600);
		assertThat(saved.getEscapeScheduleStat().getLastMonthTopTheme()).isEqualTo(null);
		assertThat(saved.getEscapeScheduleStat().getLastMonthTopSatisfaction()).isEqualTo(0);
	}
}
