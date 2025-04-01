package com.ddobang.backend.domain.theme.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.ddobang.backend.domain.theme.entity.Theme;

import lombok.Builder;

/**
 * ThemesResponse
 * 테마 다건 조회 응답에 사용할 dto
 * @author 100minha
 */
@Builder
public record ThemesResponse(
	Long id,
	String name,
	String storeName,
	int runtime,
	String recommendedParticipants,
	List<String> tags,
	String thumbnailUrl
) {

	public static ThemesResponse of(Theme theme) {
		return ThemesResponse.builder()
			.id(theme.getId())
			.name(theme.getName())
			.storeName(theme.getStore().getName())
			.runtime(theme.getRuntime())
			.recommendedParticipants(
				theme.getMinParticipants() + "~" + theme.getMaxParticipants() + "인")
			.thumbnailUrl(theme.getThumbnailUrl())
			.tags(theme.getThemeTagMappings().stream()
				.map(ttm -> ttm.getThemeTag().getName())
				.collect(Collectors.toList()))
			.build();
	}
}
