package com.ddobang.backend.domain.diary.repository;

import static com.ddobang.backend.domain.diary.entity.QDiaryStat.*;
import static com.ddobang.backend.domain.theme.entity.QTheme.*;
import static com.ddobang.backend.domain.theme.entity.QThemeTag.*;
import static com.ddobang.backend.domain.theme.entity.QThemeTagMapping.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiaryStatRepositoryImpl implements DiaryStatRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Tuple> top5TagCountSuccessCountByMember(Long authorId) {
		NumberExpression<Long> playCount = diaryStat.countDistinct();
		NumberExpression<Integer> successCount = Expressions.numberTemplate(
			Integer.class,
			"sum(case when {0} = true then 1 else 0 end)",
			diaryStat.escapeResult
		);

		List<Tuple> result = queryFactory
			.select(
				themeTag.name,
				playCount,
				successCount
			)
			.from(diaryStat)
			.join(diaryStat.theme, theme)
			.join(theme.themeTagMappings, themeTagMapping)
			.join(themeTagMapping.themeTag, themeTag)
			.where(diaryStat.author.id.eq(authorId))
			.groupBy(themeTag.name)
			.orderBy(playCount.desc())
			.limit(5)
			.fetch();

		return result;
	}

	@Override
	public Long countTotalGenreBaseByMember(Long authorId) {
		return countGenreAppearancesByMember(authorId) + countNoGenreDiaryStatsByMember(authorId);
	}

	private Long countGenreAppearancesByMember(Long authorId) {
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

	private Long countNoGenreDiaryStatsByMember(Long authorId) {
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
}
