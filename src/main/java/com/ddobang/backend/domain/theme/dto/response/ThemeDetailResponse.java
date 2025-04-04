package com.ddobang.backend.domain.theme.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.ddobang.backend.domain.theme.dto.ThemeStatDto;
import com.ddobang.backend.domain.theme.entity.Theme;

import lombok.Builder;

/**
 * ThemeResponse
 * 테마 상세 조회 응답에 사용할 dto
 * @author 100minha
 */
@Builder
public record ThemeDetailResponse(
	String name,
	String storeName,
	int runtime,
	String recommendedParticipants,
	List<String> tags,
	String thumbnailUrl,
	float officialDifficulty,
	ThemeStatDto diaryBasedThemeStat
) {
	public static ThemeDetailResponse of(Theme theme, ThemeStatDto themeStatDto) {
		return ThemeDetailResponse.builder()
			.name(theme.getName())
			.storeName(theme.getStore().getName())
			.runtime(theme.getRuntime())
			.recommendedParticipants(
				theme.getMinParticipants() + "~" + theme.getMaxParticipants() + "인")
			.thumbnailUrl(theme.getThumbnailUrl())
			.tags(theme.getThemeTagMappings().stream()
				.map(ttm -> ttm.getThemeTag().getName())
				.collect(Collectors.toList()))
			.officialDifficulty(theme.getOfficialDifficulty())
			.diaryBasedThemeStat(themeStatDto)
			.build();
	}
}
