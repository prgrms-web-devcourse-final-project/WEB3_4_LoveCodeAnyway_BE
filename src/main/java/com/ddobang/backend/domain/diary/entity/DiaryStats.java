package com.ddobang.backend.domain.diary.entity;

import com.ddobang.backend.domain.diary.dto.request.DiaryRequestDto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DiaryStats {
	@Id
	private Long id;

	@OneToOne
	@MapsId
	private Diary diary;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "theme_id", insertable = false, updatable = false)
	// private Theme theme;

	// 평가 관련 필드
	private int difficulty;
	private int fear;
	private int activity;
	private int satisfaction;
	private int production;
	private int story;
	private int question;
	private int interior;
	private int deviceRatio;

	// 탈출 관련 데이터
	private int hintCount;
	private boolean escapeResult;
	private int elapsedTime;

	@Builder
	public DiaryStats(
		Diary diary,
		int difficulty,
		int fear,
		int activity,
		int satisfaction,
		int production,
		int story,
		int question,
		int interior,
		int deviceRatio,
		int hintCount,
		boolean escapeResult,
		int elapsedTime
	) {
		this.diary = diary;
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
	}

	public void modify(
		DiaryRequestDto diaryRequestDto,
		int elapsedTime
	) {
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
	}
}
