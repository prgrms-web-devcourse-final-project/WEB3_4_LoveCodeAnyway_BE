package com.ddobang.backend.domain.themewish.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.store.entity.QStore;
import com.ddobang.backend.domain.theme.entity.QTheme;
import com.ddobang.backend.domain.theme.entity.QThemeTagMapping;
import com.ddobang.backend.domain.theme.tag.entity.QThemeTag;
import com.ddobang.backend.domain.themewish.entity.QThemeWish;
import com.ddobang.backend.domain.themewish.entity.ThemeWish;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * ThemeWishRepositoryImpl
 * <p></p>
 * @author 100minha
 */
@Repository
@RequiredArgsConstructor
public class ThemeWishRepositoryImpl implements ThemeWishRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private static final QThemeWish themeWish = QThemeWish.themeWish;
	private static final QTheme theme = QTheme.theme;
	private static final QStore store = QStore.store;
	private static final QThemeTagMapping themeTagMapping = QThemeTagMapping.themeTagMapping;
	private static final QThemeTag themeTag = QThemeTag.themeTag;

	@Override
	public List<ThemeWish> findThemesWishedByMemberId(Long memberId) {
		return queryFactory
			.selectDistinct(themeWish)
			.from(themeWish)
			.join(themeWish.theme, theme).fetchJoin()
			.join(theme.store, store).fetchJoin()
			.leftJoin(theme.themeTagMappings, themeTagMapping).fetchJoin()
			.leftJoin(themeTagMapping.themeTag, themeTag).fetchJoin()
			.where(themeWish.member.id.eq(memberId))
			.fetch();
	}
}
