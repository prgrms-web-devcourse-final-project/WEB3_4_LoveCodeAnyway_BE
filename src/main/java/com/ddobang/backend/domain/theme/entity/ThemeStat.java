package com.ddobang.backend.domain.theme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ThemeStats
 * 테마통계 엔티티
 * @author 100minha
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeStat {
	@Id
	@Column(name = "theme_id")
	private Long id;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id")
	private Theme theme;

	@Version
	private Long version;

	@PositiveOrZero
	@Max(5)
	private float difficulty;
	@PositiveOrZero
	@Max(5)
	private float fear;
	@PositiveOrZero
	@Max(5)
	private float activity;
	@PositiveOrZero
	@Max(5)
	private float satisfaction;
	@PositiveOrZero
	@Max(5)
	private float production;
	@PositiveOrZero
	@Max(5)
	private float story;
	@PositiveOrZero
	@Max(5)
	private float question;
	@PositiveOrZero
	@Max(5)
	private float interior;
	private float deviceRatio;
	private int noHintEscapeRate;
	private int escapeResult;
	private int escapeTimeAvg;

	private int diaryCount;

	@Builder
	public ThemeStat(Theme theme, float difficulty, float fear, float activity, float satisfaction, float production,
		float story, float question, float interior, float deviceRatio, int noHintEscapeRate, int escapeResult,
		int escapeTimeAvg, int diaryCount) {
		this.theme = theme;
		this.difficulty = difficulty;
		this.fear = fear;
		this.activity = activity;
		this.satisfaction = satisfaction;
		this.production = production;
		this.story = story;
		this.question = question;
		this.interior = interior;
		this.deviceRatio = deviceRatio;
		this.noHintEscapeRate = noHintEscapeRate;
		this.escapeResult = escapeResult;
		this.escapeTimeAvg = escapeTimeAvg;
		this.diaryCount = diaryCount;
	}

	public void updateStat(float difficulty, float fear, float activity, float satisfaction, float production,
		float story,
		float question, float interior, float deviceRatio, int noHintEscapeRate, int escapeResult, int escapeTimeAvg,
		int diaryCount) {
		this.difficulty = difficulty;
		this.fear = fear;
		this.activity = activity;
		this.satisfaction = satisfaction;
		this.production = production;
		this.story = story;
		this.question = question;
		this.interior = interior;
		this.deviceRatio = deviceRatio;
		this.noHintEscapeRate = noHintEscapeRate;
		this.escapeResult = escapeResult;
		this.escapeTimeAvg = escapeTimeAvg;
		this.diaryCount = diaryCount;
	}
}
