package com.ddobang.backend.domain.diary.initdata;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.diary.dto.DiaryRequestDto;
import com.ddobang.backend.domain.diary.service.DiaryService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DiaryInitData {
	private final DiaryService diaryService;

	@Autowired
	@Lazy
	private DiaryInitData self;

	@Bean
	public ApplicationRunner diaryInitDataApplicationRunner() {
		return args -> {
			self.initData();
		};
	}

	@Transactional
	public void initData() {
		if (diaryService.count() > 0) {
			return;
		}

		DiaryRequestDto diaryRequestDto = new DiaryRequestDto(
			1L,
			"https://placehold.co/640x640?text=:P",
			LocalDate.of(2025, 2, 20),
			"지인1, 지인2",
			3,
			3,
			3,
			3,
			3,
			3,
			3,
			3,
			70,
			1,
			true,
			"elapsed",
			3600,
			"너무 재밌었다!!"
		);

		diaryService.write(diaryRequestDto);
	}
}
