package com.ddobang.backend.domain.theme.dto.response;

import java.util.List;

import com.ddobang.backend.domain.theme.entity.Theme;

import lombok.Builder;

/**
 * ThemeForPartyResponse
 * 모임 등록 에서 테마 검색 응답 dto
 * @author 100minha
 */
@Builder
public record ThemeForPartyResponse(
	Long themeId,
	String name,
	String storeName,
	List<String> tags
) {

	public static ThemeForPartyResponse of(Theme theme) {
		return ThemeForPartyResponse.builder()
			.themeId(theme.getId())
			.name(theme.getName())
			.storeName(theme.getStore().getName())
			.tags(theme.getThemeTagMappings().stream()
				.map(ttm -> ttm.getThemeTag().getName())
				.toList())
			.build();
	}
}
