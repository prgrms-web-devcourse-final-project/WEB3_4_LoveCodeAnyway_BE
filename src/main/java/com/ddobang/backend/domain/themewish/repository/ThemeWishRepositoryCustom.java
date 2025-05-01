package com.ddobang.backend.domain.themewish.repository;

import java.util.List;

import com.ddobang.backend.domain.themewish.entity.ThemeWish;

/**
 * ThemeWishRepositoryCustom
 * <p></p>
 * @author 100minha
 */
public interface ThemeWishRepositoryCustom {

	List<ThemeWish> findThemesWishedByMemberId(Long memberId);
}
