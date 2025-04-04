package com.ddobang.backend.domain.theme.dto.response;

import com.ddobang.backend.domain.theme.entity.Theme;

/**
 * ThemeForDiaryResponse
 * 일지 등록 에서 테마 검색 응답 dto
 * @author 100minha
 */
public record ThemeForDiaryResponse(
	Long themeId,
	String themeName,
	String storeName
) {
	public static ThemeForDiaryResponse of(Theme theme) {
		return new ThemeForDiaryResponse(theme.getId(), theme.getName(), theme.getStore().getName());
	}
}
