package com.ddobang.backend.domain.theme.dto.response;

import java.util.List;

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
		int min = theme.getMinParticipants();
		int max = theme.getMaxParticipants();

		String recommendedParticipants = (min > 0 && max > 0) ? String.format("%d~%d인", min, max)
			: (min > 0) ? String.format("%d인~", min)
			: (max > 0) ? String.format("~%d인", max)
			: "?인";

		return ThemesResponse.builder()
			.id(theme.getId())
			.name(theme.getName())
			.storeName(theme.getStore().getName())
			.runtime(theme.getRuntime())
			.recommendedParticipants(recommendedParticipants)
			.thumbnailUrl(theme.getThumbnailUrl())
			.tags(theme.getThemeTagMappings().stream()
				.map(ttm -> ttm.getThemeTag().getName())
				.toList())
			.build();
	}
}
