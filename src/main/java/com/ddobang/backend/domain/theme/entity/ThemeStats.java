package com.ddobang.backend.domain.theme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
public class ThemeStats {
	@Id
	@Column(name = "theme_id")
	private Long id;

	@MapsId
	@OneToOne
	@JoinColumn(name = "theme_id")
	private Theme theme;

	private float difficulty;
	private float fear;
	private float activity;
	private float satisfaction;
	private float production;
	private float story;
	private float question;
	private float interior;
	private float deviceRatio;
	private float noHintEscapeRate;
	private float escapeResult;
	private float escapeTimeAvg;

	@Builder
	public ThemeStats(Theme theme, float difficulty, float fear, float activity, float satisfaction, float production,
		float story, float question, float interior, float deviceRatio, float noHintEscapeRate, float escapeResult,
		float escapeTimeAvg) {
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
	}
}
