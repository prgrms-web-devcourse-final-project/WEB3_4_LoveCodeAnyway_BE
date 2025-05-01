package com.ddobang.backend.domain.theme.repository;

import java.util.List;

import com.ddobang.backend.domain.theme.dto.request.ThemeFilterRequest;
import com.ddobang.backend.domain.theme.dto.response.SimpleThemeResponse;
import com.ddobang.backend.domain.theme.entity.Theme;

/**
 * ThemeRepositoryCustom
 * 쿼리 dsl 메서드 저장용 인터페이스
 * @author 100minha
 */
public interface ThemeRepositoryCustom {
	List<Theme> findThemesByFilter(ThemeFilterRequest request, int page, int size);

	List<Theme> findThemesForPartySearch(String keyword);

	List<SimpleThemeResponse> findThemesForAdminSearch(ThemeFilterRequest request, int page, int size);

	List<Theme> findTop10PopularThemesByTagName(String tagName);

	List<Theme> findTop10NewestThemesByTagName(String tagName);
}
