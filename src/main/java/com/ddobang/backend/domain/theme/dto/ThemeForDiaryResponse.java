package com.ddobang.backend.domain.theme.dto;

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
}
