package com.ddobang.backend.domain.diary.entity;

import com.ddobang.backend.domain.theme.entity.Theme;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;

@Entity
@Getter
public class DiaryStats {
	@OneToOne
	@MapsId
	private Diary diary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id", insertable = false, updatable = false)
	private Theme theme;

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
}
