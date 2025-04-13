package com.ddobang.backend.domain.diary.repository;

import static com.ddobang.backend.domain.diary.entity.QDiary.*;
import static com.ddobang.backend.domain.diary.entity.QDiaryStat.*;
import static com.ddobang.backend.domain.region.entity.QRegion.*;
import static com.ddobang.backend.domain.store.entity.QStore.*;
import static com.ddobang.backend.domain.theme.entity.QTheme.*;
import static com.ddobang.backend.domain.theme.entity.QThemeTag.*;
import static com.ddobang.backend.domain.theme.entity.QThemeTagMapping.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.diary.dto.request.DiaryFilterRequest;
import com.ddobang.backend.domain.diary.entity.Diary;
import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.theme.entity.QThemeTag;
import com.ddobang.backend.domain.theme.entity.QThemeTagMapping;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Diary> findDiariesByFilter(Member author, DiaryFilterRequest request, Pageable pageable) {
		BooleanBuilder builder = buildFilterConditions(author, request);

		JPAQuery<Diary> diariesQuery = createDiariesQuery(builder, request);

		applySorting(pageable, diariesQuery);
		diariesQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

		JPAQuery<Long> totalQuery = createTotalQuery(builder, request);

		return PageableExecutionUtils.getPage(diariesQuery.fetch(), pageable, totalQuery::fetchOne);
	}

	private BooleanBuilder buildFilterConditions(Member author, DiaryFilterRequest request) {
		BooleanBuilder builder = new BooleanBuilder();

		// 작성자 확인
		if (author != null) {
			builder.and(diary.author.id.eq(author.getId()));
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
					subMapping.theme.eq(diary.theme),    // 지금 조회 중인 테마와 매핑된 태그인지 확인
					subTag.id.in(request.tagIds())    // 사용자가 요청한 필터에 포함되는 태그인지 확인
				)
				.exists()    // where 조건 만족 시 true
			);
		}

		// 기간 필터링
		if (request.startDate() != null && request.endDate() != null) {
			builder.and(diary.diaryStat.escapeDate.between(request.startDate(), request.endDate()));
		}

		// 성공 여부 필터링
		// 값이 success / fail이 아니거나 null 일 경우 전체 조회
		if (request.isSuccess() != null) {
			if ("success".equalsIgnoreCase(request.isSuccess())) {
				builder.and(diary.diaryStat.escapeResult.eq(true));
			} else if ("fail".equalsIgnoreCase(request.isSuccess())) {
				builder.and(diary.diaryStat.escapeResult.eq(false));
			}
		}

		// 노힌트 여부 필터링
		if (request.isNoHint() != null && request.isNoHint()) {
			builder.and(diary.diaryStat.hintCount.eq(0));
		}

		// 검색
		if (request.keyword() != null && !request.keyword().isBlank()) {
			builder.and(
				diary.theme.name.containsIgnoreCase(request.keyword())
					.or(diary.theme.store.name.containsIgnoreCase(request.keyword()))
			);
		}

		return builder;
	}

	private JPAQuery<Diary> createDiariesQuery(BooleanBuilder builder, DiaryFilterRequest request) {
		JPAQuery<Diary> query = queryFactory
			.select(diary)
			.from(diary)
			.join(diary.theme, theme).fetchJoin()
			.distinct();

		applyJoins(query, request, true);

		return query.where(builder);
	}

	private void applySorting(Pageable pageable, JPAQuery<Diary> diariesQuery) {
		for (Sort.Order o : pageable.getSort()) {
			PathBuilder pathBuilder = new PathBuilder(diary.getType(), diary.getMetadata());
			diariesQuery.orderBy(
				new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
		}
	}

	private JPAQuery<Long> createTotalQuery(BooleanBuilder builder, DiaryFilterRequest request) {
		JPAQuery<Long> query = queryFactory
			.select(diary.id.countDistinct())
			.from(diary)
			.join(diary.theme, theme);

		applyJoins(query, request, false);

		return query.where(builder);
	}

	// 공통적인 join 로직을 적용하는 메서드
	private <T> void applyJoins(JPAQuery<T> query, DiaryFilterRequest request, boolean fetchJoin) {
		if (request.regionId() != null && !request.regionId().isEmpty()) {
			if (fetchJoin) {
				query.join(theme.store, store).fetchJoin();
				query.join(store.region, region).fetchJoin();
			} else {
				query.join(theme.store, store);
				query.join(store.region, region);
			}
		}

		if (request.tagIds() != null && !request.tagIds().isEmpty()) {
			query.join(theme.themeTagMappings, themeTagMapping);
			query.join(themeTagMapping.themeTag, themeTag);
			if (fetchJoin) {
				query.fetchJoin();
			}
		}
		if (request.isSuccess() != null
			|| (request.isNoHint() != null && request.isNoHint())
			|| (request.startDate() != null && request.endDate() != null)) {
			if (fetchJoin) {
				query.join(diary.diaryStat, diaryStat).fetchJoin();
			} else {
				query.join(diary.diaryStat, diaryStat);
			}
		}
	}
}
