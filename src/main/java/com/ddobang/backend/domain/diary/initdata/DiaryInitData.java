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
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DiaryInitData {
	private final DiaryService diaryService;
	private final MemberRepository memberRepository;

	@Autowired
	@Lazy
	private DiaryInitData self;

	@Bean
	public ApplicationRunner diaryInitDataApplicationRunner() {
		return args -> {
			self.memberInitData();
			self.initData();
		};
	}

	@Transactional
	public void memberInitData() {
		if (memberRepository.count() > 0) {
			return;
		}

		//테스트용 회원 생성
		Member member = Member.builder()
			.nickname("testUser1")
			.build();

		memberRepository.save(member);
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
