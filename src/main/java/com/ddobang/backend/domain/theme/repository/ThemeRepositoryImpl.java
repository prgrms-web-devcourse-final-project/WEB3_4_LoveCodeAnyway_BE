package com.ddobang.backend.domain.theme.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.region.entity.QRegion;
import com.ddobang.backend.domain.store.entity.QStore;
import com.ddobang.backend.domain.theme.dto.request.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.entity.QTheme;
import com.ddobang.backend.domain.theme.entity.QThemeStat;
import com.ddobang.backend.domain.theme.entity.QThemeTag;
import com.ddobang.backend.domain.theme.entity.QThemeTagMapping;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * ThemeRepositoryImpl
 * 쿼리 dsl 메서드 선언용 구현체 클래스
 * @author 100minha
 */
@Repository
@RequiredArgsConstructor
public class ThemeRepositoryImpl implements ThemeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private static final QTheme theme = QTheme.theme;
	private static final QStore store = QStore.store;
	private static final QRegion region = QRegion.region;
	private static final QThemeTagMapping mapping = QThemeTagMapping.themeTagMapping;
	private static final QThemeTag tag = QThemeTag.themeTag;
	private static final QThemeStat themeStat = QThemeStat.themeStat;

	@Override
	public List<Theme> findThemesByFilter(ThemeFilterRequest request, int page, int size) {
		BooleanBuilder condition = buildFilterConditions(request, false);

		return queryFactory
			.selectFrom(theme)
			.leftJoin(theme.store, store).fetchJoin()
			.leftJoin(store.region, region).fetchJoin()
			.leftJoin(theme.themeTagMappings, mapping).fetchJoin()
			.leftJoin(mapping.themeTag, tag).fetchJoin()
			.distinct()
			.where(condition)
			.orderBy(theme.createdAt.desc())
			.offset((long)page * size)
			.limit(size + 1) // size보다 1개 더 가져와서 hasNext 판단
			.fetch();
	}

	@Override
	public List<Theme> findThemesForPartySearch(String keyword) {
		BooleanBuilder condition = buildSearchForPartyConditions(keyword);

		return queryFactory
			.selectFrom(theme)
			.leftJoin(theme.store, store).fetchJoin()
			.leftJoin(theme.themeTagMappings, mapping).fetchJoin()
			.leftJoin(mapping.themeTag, tag).fetchJoin()
			.where(condition)
			.distinct()
			.fetch();
	}

	@Override
	public List<SimpleThemeResponse> findThemesForAdminSearch(ThemeFilterRequest request, int page, int size) {
		BooleanBuilder condition = buildFilterConditions(request, true);

		return queryFactory
			.select(Projections.constructor(
				SimpleThemeResponse.class,
				theme.id,
				theme.name,
				store.name
			))
			.from(theme)
			.leftJoin(theme.store, store)
			.leftJoin(store.region, region)
			.where(condition)
			.orderBy(theme.name.asc())
			.offset((long)page * size)
			.limit(size + 1) // size보다 1개 더 가져와서 hasNext 판단
			.fetch();
	}

	@Override
	public List<Theme> findTop10PopularThemesByTagName(String tagName) {
		BooleanBuilder condition = buildForLandingConditions(tagName);

		List<ThemeStat> themeStats = queryFactory
			.select(themeStat)
			.from(themeStat)
			.join(themeStat.theme, theme).fetchJoin()
			.leftJoin(theme.store, store).fetchJoin()
			.leftJoin(theme.themeTagMappings, mapping).fetchJoin()
			.leftJoin(mapping.themeTag, tag).fetchJoin()
			.where(condition)
			.orderBy(
				themeStat.diaryCount.multiply(7)
					.add(themeStat.satisfaction.multiply(3))
					.desc()
			)
			.limit(10)
			.fetch();

		return themeStats.stream()
			.map(ThemeStat::getTheme)
			.toList();
	}

	@Override
	public List<Theme> findTop10NewestThemesByTagName(String tagName) {
		BooleanBuilder condition = buildForLandingConditions(tagName);

		return queryFactory
			.selectFrom(theme)
			.join(theme.store, store).fetchJoin()
			.leftJoin(theme.themeTagMappings, mapping).fetchJoin()
			.leftJoin(mapping.themeTag, tag).fetchJoin()
			.where(condition)
			.orderBy(theme.createdAt.desc())
			.limit(10)
			.fetch();
	}

	private BooleanBuilder buildFilterConditions(ThemeFilterRequest request, Boolean isForAdmin) {
		BooleanBuilder builder = new BooleanBuilder();

		if (isForAdmin) {
			builder.and(theme.status.ne(Theme.Status.DELETED));
		} else {
			builder.and(theme.status.eq(Theme.Status.OPENED));
		}

		// 지역 필터링
		if (request.regionId() != null && !request.regionId().isEmpty()) {
			builder.and(store.region.id.in(request.regionId()));
		}

		// 태그 필터링 시 사용될 서브 쿼리
		// 요청에 포함된 태그들 중 하나라도 포함되면 통과
		if (request.tagIds() != null && !request.tagIds().isEmpty()) {
			QThemeTagMapping subMapping = new QThemeTagMapping("subMapping");
			QThemeTag subTag = new QThemeTag("subTag");

			builder.and(JPAExpressions
				.selectOne()    // 존재 여부만 판단
				.from(subMapping)
				.join(subMapping.themeTag, subTag)    // 테마 태그에 조인
				.where(
					subMapping.theme.eq(theme),    // 지금 조회 중인 테마와 매핑된 태그인지 확인
					subTag.id.in(request.tagIds())    // 사용자가 요청한 필터에 포함되는 태그인지 확인
				)
				.exists()    // where 조건 만족 시 true
			);
		}

		if (request.participants() != null) {
			// 최대 인원 수가 0인 경우, 20명으로 간주
			NumberExpression<Integer> adjustedMaxParticipants = new CaseBuilder()
				.when(theme.maxParticipants.eq(0))
				.then(20)
				.otherwise(theme.maxParticipants);

			builder.and(
				theme.minParticipants.loe(request.participants())
					.and(adjustedMaxParticipants.goe(request.participants()))
			);
		}

		if (request.keyword() != null && !request.keyword().isBlank()) {
			builder.and(
				theme.name.containsIgnoreCase(request.keyword())
					.or(store.name.containsIgnoreCase(request.keyword()))    //알파벳 대소문자 구분x
			);
		}

		return builder;
	}

	private BooleanBuilder buildSearchForPartyConditions(String keyword) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(theme.status.eq(Theme.Status.OPENED));

		if (keyword != null && !keyword.isBlank()) {
			builder.and(
				theme.name.containsIgnoreCase(keyword)
					.or(store.name.containsIgnoreCase(keyword))        //알파벳 대소문자 구분x
			);
		}

		return builder;
	}

	private BooleanBuilder buildForLandingConditions(String tagName) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(theme.status.eq(Theme.Status.OPENED));

		if (tagName != null && !tagName.isBlank()) {
			QThemeTagMapping subMapping = new QThemeTagMapping("subMapping");
			QThemeTag subTag = new QThemeTag("subTag");

			builder.and(JPAExpressions
				.selectOne()    // 존재 여부만 판단
				.from(subMapping)
				.join(subMapping.themeTag, subTag)    // 테마 태그에 조인
				.where(
					subMapping.theme.eq(theme),    // 지금 조회 중인 테마와 매핑된 태그인지 확인
					subTag.name.eq(tagName)    // 사용자가 요청한 필터에 포함되는 태그인지 확인
				)
				.exists()    // where 조건 만족 시 true
			);
		}

		return builder;
	}

}
