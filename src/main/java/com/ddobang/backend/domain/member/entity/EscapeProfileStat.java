package com.ddobang.backend.domain.member.entity;

import java.util.Map;

import com.ddobang.backend.domain.member.dto.stat.EscapeProfileStatDto;
import com.ddobang.backend.global.converter.MapStrIntToJsonConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class EscapeProfileStat {
	// 성향
	private double tendencyStimulating; // 성향 - 자극형
	private double tendencyLogical; // 성향 - 논리형
	private double tendencyNarrative; // 성향 - 서사형
	private double tendencyActive; // 성향 - 활동형
	private double tendencySpatial; // 성향 - 공간중시형

	// 장르별 데이터
	@Column(columnDefinition = "TEXT")
	@Convert(converter = MapStrIntToJsonConverter.class)
	private Map<String, Integer> genreCountMap; // 장르별 참여 비율

	@Column(columnDefinition = "TEXT")
	@Convert(converter = MapStrIntToJsonConverter.class)
	private Map<String, Integer> genreSuccessMap; // 장르별 성공/실패 비율

	// 난이도별 평균 힌트
	private double difficultyHintAvg1; // 난이도별 평균 힌트 - 매우 쉬움
	private double difficultyHintAvg2; // 난이도별 평균 힌트 - 쉬움
	private double difficultyHintAvg3; // 난이도별 평균 힌트 - 보통
	private double difficultyHintAvg4; // 난이도별 평균 힌트 - 어려움
	private double difficultyHintAvg5; // 난이도별 평균 힌트 - 매우 어려움

	// 난이도별 만족도
	private double difficultySatisAvg1; // 난이도별 만족도 - 매우 쉬움
	private double difficultySatisAvg2; // 난이도별 만족도 - 쉬움
	private double difficultySatisAvg3; // 난이도별 만족도 - 보통
	private double difficultySatisAvg4; // 난이도별 만족도 - 어려움
	private double difficultySatisAvg5; // 난이도별 만족도 - 매우 어려움

	@Builder
	public EscapeProfileStat(
		double tendencyStimulating,
		double tendencyLogical,
		double tendencyNarrative,
		double tendencyActive,
		double tendencySpatial,
		Map<String, Integer> genreCountMap,
		Map<String, Integer> genreSuccessMap,
		double difficultyHintAvg1,
		double difficultyHintAvg2,
		double difficultyHintAvg3,
		double difficultyHintAvg4,
		double difficultyHintAvg5,
		double difficultySatisAvg1,
		double difficultySatisAvg2,
		double difficultySatisAvg3,
		double difficultySatisAvg4,
		double difficultySatisAvg5
	) {
		this.tendencyStimulating = tendencyStimulating;
		this.tendencyLogical = tendencyLogical;
		this.tendencyNarrative = tendencyNarrative;
		this.tendencyActive = tendencyActive;
		this.tendencySpatial = tendencySpatial;
		this.genreCountMap = genreCountMap;
		this.genreSuccessMap = genreSuccessMap;
		this.difficultyHintAvg1 = difficultyHintAvg1;
		this.difficultyHintAvg2 = difficultyHintAvg2;
		this.difficultyHintAvg3 = difficultyHintAvg3;
		this.difficultyHintAvg4 = difficultyHintAvg4;
		this.difficultyHintAvg5 = difficultyHintAvg5;
		this.difficultySatisAvg1 = difficultySatisAvg1;
		this.difficultySatisAvg2 = difficultySatisAvg2;
		this.difficultySatisAvg3 = difficultySatisAvg3;
		this.difficultySatisAvg4 = difficultySatisAvg4;
		this.difficultySatisAvg5 = difficultySatisAvg5;
	}

	public void update(EscapeProfileStatDto escapeProfileStatDto) {
		this.tendencyStimulating = escapeProfileStatDto.tendencyStimulating();
		this.tendencyLogical = escapeProfileStatDto.tendencyLogical();
		this.tendencyNarrative = escapeProfileStatDto.tendencyNarrative();
		this.tendencyActive = escapeProfileStatDto.tendencyActive();
		this.tendencySpatial = escapeProfileStatDto.tendencySpatial();
		this.genreCountMap = escapeProfileStatDto.genreCountMap();
		this.genreSuccessMap = escapeProfileStatDto.genreSuccessMap();
		this.difficultyHintAvg1 = escapeProfileStatDto.difficultyHintAvg1();
		this.difficultyHintAvg2 = escapeProfileStatDto.difficultyHintAvg2();
		this.difficultyHintAvg3 = escapeProfileStatDto.difficultyHintAvg3();
		this.difficultyHintAvg4 = escapeProfileStatDto.difficultyHintAvg4();
		this.difficultyHintAvg5 = escapeProfileStatDto.difficultyHintAvg5();
		this.difficultySatisAvg1 = escapeProfileStatDto.difficultySatisAvg1();
		this.difficultySatisAvg2 = escapeProfileStatDto.difficultySatisAvg2();
		this.difficultySatisAvg3 = escapeProfileStatDto.difficultySatisAvg3();
		this.difficultySatisAvg4 = escapeProfileStatDto.difficultySatisAvg4();
		this.difficultySatisAvg5 = escapeProfileStatDto.difficultySatisAvg5();
	}
}
