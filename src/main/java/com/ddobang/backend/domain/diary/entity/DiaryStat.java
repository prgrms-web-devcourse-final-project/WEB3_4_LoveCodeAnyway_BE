package com.ddobang.backend.domain.diary.entity;

import java.time.LocalDate;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.theme.entity.Theme;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DiaryStat {
	@Id
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	private Diary diary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id", nullable = false)
	private Theme theme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	private Member author;

	// 평가 관련 필드
	private int difficulty;
	private int fear;
	private int activity;
	private int satisfaction;
	private int production;
	private int story;
	private int question;
	private int interior;
	private Integer deviceRatio;

	// 탈출 관련 데이터
	private Integer hintCount;
	private boolean escapeResult;
	private int elapsedTime;
	private LocalDate escapeDate;

	@Builder
	public DiaryStat(
		Diary diary,
		Theme theme,
		Member author,
		int difficulty,
		int fear,
		int activity,
		int satisfaction,
		int production,
		int story,
		int question,
		int interior,
		Integer deviceRatio,
		Integer hintCount,
		boolean escapeResult,
		int elapsedTime,
		LocalDate escapeDate
	) {
		this.diary = diary;
		this.theme = theme;
		this.author = author;
		this.difficulty = difficulty;
		this.fear = fear;
		this.activity = activity;
		this.satisfaction = satisfaction;
		this.production = production;
		this.story = story;
		this.question = question;
		this.interior = interior;
		this.deviceRatio = deviceRatio;
		this.hintCount = hintCount;
		this.escapeResult = escapeResult;
		this.elapsedTime = elapsedTime;
		this.escapeDate = escapeDate;
	}

	public static DiaryStat toDiaryStat(Diary diary, DiaryRequestDto dto, int elapsedTime) {
		return DiaryStat.builder()
			.diary(diary)
			.theme(diary.getTheme())
			.author(diary.getAuthor())
			.difficulty(dto.difficulty())
			.fear(dto.fear())
			.activity(dto.activity())
			.satisfaction(dto.satisfaction())
			.production(dto.production())
			.story(dto.story())
			.question(dto.question())
			.interior(dto.interior())
			.deviceRatio(dto.deviceRatio())
			.hintCount(dto.hintCount())
			.escapeResult(dto.escapeResult())
			.elapsedTime(elapsedTime)
			.escapeDate(dto.escapeDate())
			.build();
	}

	public void modify(
		DiaryRequestDto diaryRequestDto,
		int elapsedTime
	) {
		this.theme = this.diary.getTheme();
		this.difficulty = diaryRequestDto.difficulty();
		this.fear = diaryRequestDto.fear();
		this.activity = diaryRequestDto.activity();
		this.satisfaction = diaryRequestDto.satisfaction();
		this.production = diaryRequestDto.production();
		this.story = diaryRequestDto.story();
		this.question = diaryRequestDto.question();
		this.interior = diaryRequestDto.interior();
		this.deviceRatio = diaryRequestDto.deviceRatio();
		this.hintCount = diaryRequestDto.hintCount();
		this.escapeResult = diaryRequestDto.escapeResult();
		this.elapsedTime = elapsedTime;
		this.escapeDate = diaryRequestDto.escapeDate();
	}
}
