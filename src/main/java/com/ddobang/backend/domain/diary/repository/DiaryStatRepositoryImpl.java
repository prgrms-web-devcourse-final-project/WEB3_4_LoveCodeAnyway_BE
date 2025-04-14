package com.ddobang.backend.domain.diary.repository;

import static com.ddobang.backend.domain.diary.entity.QDiaryStat.*;
import static com.ddobang.backend.domain.theme.entity.QTheme.*;
import static com.ddobang.backend.domain.theme.entity.QThemeStat.*;
import static com.ddobang.backend.domain.theme.entity.QThemeTag.*;
import static com.ddobang.backend.domain.theme.entity.QThemeTagMapping.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiaryStatRepositoryImpl implements DiaryStatRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Tuple> top5TagCountSuccessCountByMember(long authorId) {
		NumberExpression<Long> playCount = diaryStat.id.countDistinct();
		NumberExpression<Integer> successCount = Expressions.numberTemplate(
			Integer.class,
			"sum(case when {0} = true then 1 else 0 end)",
			diaryStat.escapeResult
		);

		List<Tuple> result = queryFactory
			.select(
				themeTag.name, // 태그 이름
				playCount, // 해당 태그를 플레이한 횟수
				successCount // 해당 태그를 성공한 횟수
			)
			.from(diaryStat)
			.join(diaryStat.theme, theme)
			.join(theme.themeTagMappings, themeTagMapping)
			.join(themeTagMapping.themeTag, themeTag)
			.where(diaryStat.author.id.eq(authorId))
			.groupBy(themeTag.name)
			.orderBy(playCount.desc()) // 내림차순으로
			.limit(5) // 상위 5개만 선정
			.fetch();

		return result;
	}

	@Override // 장르 기준 전체 테마의 수(한 테마의 장르가 여러개인 경우 중복 포함)
	public Long countTotalGenreBaseByMember(long authorId) {
		return countGenreAppearancesByMember(authorId) + countNoGenreDiaryStatsByMember(authorId);
	}

	@Override // 난이도 레벨 기준 총 테마수, 힌트 갯수
	public Map<Integer, Tuple> difficultyStatsWithHints(long authorId) {
		Map<Integer, Tuple> result = new LinkedHashMap<>();

		for (int level = 1; level <= 5; level++) {
			NumberExpression<Long> themeCount = diaryStat.id.countDistinct(); // 해당 되는 일지 수
			// 해당 되는 총 힌트 갯수(null은 제외, 0은 포함)
			NumberExpression<Integer> totalHints = Expressions.numberTemplate(
				Integer.class, "coalesce(sum(case when {0} is not null then {0} else 0 end), 0)", diaryStat.hintCount
			);

			Tuple tuple = queryFactory
				.select(
					themeCount,
					totalHints
				)
				.from(diaryStat)
				.join(diaryStat.theme, theme)
				.leftJoin(themeStat).on(themeStat.theme.eq(theme))
				.where(
					diaryStat.author.id.eq(authorId),
					difficultyRange(level), // 난이도 레벨 범위 내
					diaryStat.hintCount.isNotNull() // 힌트 개수가 null이 아닌 경우에만
				)
				.fetchOne();

			if (tuple == null) { // 값이 없을 때 기본값 저장
				tuple = Projections.tuple(
					Expressions.constant(0L),
					Expressions.constant(0)
				).newInstance(0L, 0);
			}

			result.put(level, tuple);
		}

		return result;
	}

	@Override // 난이도 레벨 기준 총 테마수, 총 만족도 점수
	public Map<Integer, Tuple> difficultyStatsWithSatisfaction(long authorId) {
		Map<Integer, Tuple> result = new LinkedHashMap<>();

		for (int level = 1; level <= 5; level++) {
			NumberExpression<Long> themeCount = diaryStat.id.countDistinct(); // 해당 되는 일지 수
			// 해당 되는 만족도의 합 (0 제외)
			NumberExpression<Integer> totalSatisfaction = Expressions.numberTemplate(
				Integer.class, "coalesce(sum(case when {0} > 0 then {0} else 0 end), 0)", diaryStat.satisfaction
			);

			Tuple tuple = queryFactory
				.select(
					themeCount,
					totalSatisfaction
				)
				.from(diaryStat)
				.join(diaryStat.theme, theme)
				.leftJoin(themeStat).on(themeStat.theme.eq(theme))
				.where(
					diaryStat.author.id.eq(authorId),
					difficultyRange(level), // 난이도 레벨 범위 내
					diaryStat.satisfaction.gt(0) // 만족도 점수가 0 초과인 경우에만
				)
				.fetchOne();

			if (tuple == null) { // 값이 없을 때 기본값 저장
				tuple = Projections.tuple(
					Expressions.constant(0L),
					Expressions.constant(0)
				).newInstance(0L, 0);
			}

			result.put(level, tuple);
		}

		return result;
	}

	// 탈출 일지 내 테마의 모든 장르들의 수
	private Long countGenreAppearancesByMember(long authorId) {
		return queryFactory
			.select(
				Expressions.numberTemplate(Long.class, "count(*)")
			)
			.from(diaryStat)
			.join(diaryStat.theme, theme)
			.join(theme.themeTagMappings, themeTagMapping)
			.where(diaryStat.author.id.eq(authorId))
			.fetchOne();
	}

	// 탈출 일지 내의 장르가 없는 일지들의 수
	private Long countNoGenreDiaryStatsByMember(long authorId) {
		return queryFactory
			.select(diaryStat.count())
			.from(diaryStat)
			.join(diaryStat.theme, theme)
			.leftJoin(theme.themeTagMappings, themeTagMapping)
			.where(
				diaryStat.author.id.eq(authorId),
				themeTagMapping.isNull()
			)
			.fetchOne();
	}

	/**
	 * 난이도 레벨
	 * 1 이상 2 미만 1
	 * 2 이상 3 미만 2
	 * 3 이상 4 미만 3
	 * 4 이상 5 미만 4
	 * 5 = 5
	 * */
	private BooleanExpression difficultyRange(int level) {
		// theme stat의 평균 난이도 기준, 해당 난이도가 없을 경우 테마의 공식 난이도 기준(null이 아닌 첫 번째 값을 반환)
		NumberExpression<Float> difficulty = themeStat.difficulty.coalesce(theme.officialDifficulty);
		// 난이도가 null이 아니고 0.0f가 아닌 것만
		BooleanExpression validDifficulty = difficulty.isNotNull().and(difficulty.ne(0.0f));

		double min = level;
		double max = (level == 5) ? 5.0 : level + 1.0;

		return validDifficulty.and(difficulty.goe((float)min).and(difficulty.lt((float)max)));
	}
}
