package com.ddobang.backend.domain.theme.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.store.entity.QStore;
import com.ddobang.backend.domain.theme.dto.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.entity.QTheme;
import com.ddobang.backend.domain.theme.entity.QThemeTag;
import com.ddobang.backend.domain.theme.entity.QThemeTagMapping;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.querydsl.core.BooleanBuilder;
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

	@Override
	public List<Theme> findThemesByFilter(ThemeFilterRequest request, int page, int size) {
		QTheme theme = QTheme.theme;
		QStore store = QStore.store;
		QThemeTagMapping mapping = QThemeTagMapping.themeTagMapping;
		QThemeTag tag = QThemeTag.themeTag;

		return queryFactory
			.selectFrom(theme)
			.leftJoin(theme.store, store).fetchJoin()
			.leftJoin(theme.themeTagMappings, mapping).fetchJoin()
			.leftJoin(mapping.themeTag, tag).fetchJoin()
			.distinct()
			.where(
				buildFilterConditions(request, theme, store, tag)
			)
			.orderBy(theme.createdAt.desc())
			.offset((long)page * size)
			.limit(size + 1) // size보다 1개 더 가져와서 hasNext 판단
			.fetch();
	}

	private BooleanBuilder buildFilterConditions(ThemeFilterRequest request, QTheme theme, QStore store,
		QThemeTag tag) {
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(theme.status.eq(Theme.Status.OPENED));

		// 지역 필터링
		if (request.regionId() != null && !request.regionId().isEmpty()) {
			builder.and(store.id.in(request.regionId()));
		}

		// 태그 필터링 시 사용될 서브 쿼리
		// 요청에 포함된 태그들 중 하나라도 포함되면 통과
		if (request.tagNames() != null && !request.tagNames().isEmpty()) {
			QThemeTagMapping subMapping = new QThemeTagMapping("subMapping");
			QThemeTag subTag = new QThemeTag("subTag");

			builder.and(JPAExpressions
				.selectOne()    // 존재 여부만 판단
				.from(subMapping)
				.join(subMapping.themeTag, subTag)    // 테마 태그에 조인
				.where(
					subMapping.theme.eq(theme),    // 지금 조회 중인 테마와 매핑된 태그인지 확인
					subTag.name.in(request.tagNames())    // 사용자가 요청한 필터에 포함되는 태그인지 확인
				)
				.exists()    // where 조건 만족 시 true
			);
		}

		if (request.participants() != null) {
			builder.and(
				theme.minParticipants.loe(request.participants())
					.and(theme.maxParticipants.goe(request.participants()))
			);
		}

		if (request.keyword() != null && !request.keyword().isBlank()) {
			builder.and(
				theme.name.containsIgnoreCase(request.keyword())
					.or(store.name.containsIgnoreCase(request.keyword()))
			);
		}

		return builder;
	}
}
