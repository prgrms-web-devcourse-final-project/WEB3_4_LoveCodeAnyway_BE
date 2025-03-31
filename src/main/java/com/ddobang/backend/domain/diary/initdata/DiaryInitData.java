package com.ddobang.backend.domain.diary.initdata;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.diary.service.DiaryService;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.repository.ThemeRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DiaryInitData {
	private final DiaryService diaryService;
	private final ThemeRepository themeRepository;

	@Autowired
	@Lazy
	private DiaryInitData self;

	@Bean
	public ApplicationRunner diaryInitDataApplicationRunner() {
		return args -> {
			self.themeInitData();
			self.initData();
		};
	}

	@Transactional
	public void themeInitData() {
		if (themeRepository.count() > 0) {
			return;
		}

		Theme theme = Theme.builder()
			.name("꼬레아 우라")
			.description("""
				'2022년 대한민국은 현재 일본 식민지 시대'
				
				우리는 일본인들의 멸시와 차별 속에서
				하루하루 힘들게 살아가고 있다.
				
				어머니가 돌아가시고 그로부터 약 1년 후 문자 하나가 왔다.
				
				"안녕하세요 저는 남자현 이라고 합니다.
				어머님(안윤복)이 자녀분들에게 남겨주신 목걸이가 필요해요.
				내일 저녁 10시 홍대꾸에 있는 아시아믹스 술집
				2번 테이블에서 기다릴게요"
				
				문득 1년 전 그날이 기억났다.
				
				다급하게 집에 들어오신 어머니
				그리고 내 손에 목걸이를 전해주시면서 하셨던 말
				
				"엄마 지인이 목걸이를 찾으면 기억했다가 꼭 전해줘야 한다."
				
				그리고 다음날 어머님은 뺑소니 사고로 돌아가셨다.
				
				어머니의 마지막 유언이 돼버렸던 말...
				
				내일 나는 목걸이를 전해줘야 한다!
				
				*편한 복장을 권장합니다.(치마 비추천)
				-추천 인원은 3인 이상입니다.
				""".stripIndent())
			.officialDifficulty(4f)
			.runtime(75)
			.recommendedParticipants("2~6")
			.minParticipants(2)
			.maxParticipants(6)
			.price(28000)
			.status(Theme.Status.OPENED)
			.reservationUrl("http://www.code-k.co.kr")
			.thumbnailUrl("http://www.code-k.co.kr/upload_file/thema/꼬레아 우라.jpg")
			.build();

		themeRepository.save(theme);
	}

	@Transactional
	public void initData() {
		if (diaryService.count() > 0) {
			return;
		}

		DiaryRequestDto diaryRequestDto = DiaryRequestDto.builder()
			.themeId(1L)
			.imageUrl("https://placehold.co/640x640?text=:P")
			.escapeDate(LocalDate.of(2025, 2, 20))
			.participants("지인1, 지인2")
			.difficulty(3)
			.fear(3)
			.activity(3)
			.satisfaction(3)
			.production(3)
			.story(3)
			.question(3)
			.interior(3)
			.deviceRatio(70)
			.hintCount(1)
			.escapeResult(true)
			.timeType("remaining")
			.elapsedTime("15:25")
			.review("너무 재밌었다!!")
			.build();

		diaryService.write(diaryRequestDto);
	}
}
