package com.ddobang.backend.domain.theme.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.ddobang.backend.domain.store.entity.Store;
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
	String description,
	int runtime,
	float officialDifficulty,
	int price,
	String recommendedParticipants,
	String thumbnailUrl,
	String reservationUrl,
	List<String> tags,
	StoreInfo storeInfo,
	ThemeStatDto diaryBasedThemeStat
) {
	public static ThemeDetailResponse of(Theme theme, ThemeStatDto themeStatDto) {
		return ThemeDetailResponse.builder()
			.name(theme.getName())
			.description(theme.getDescription())
			.runtime(theme.getRuntime())
			.officialDifficulty(theme.getOfficialDifficulty())
			.price(theme.getPrice())
			.recommendedParticipants(
				theme.getMinParticipants() + "~" + theme.getMaxParticipants() + "인")
			.thumbnailUrl(theme.getThumbnailUrl())
			.reservationUrl(theme.getReservationUrl())
			.tags(theme.getThemeTagMappings().stream()
				.map(ttm -> ttm.getThemeTag().getName())
				.collect(Collectors.toList()))
			.storeInfo(StoreInfo.of(theme.getStore()))
			.diaryBasedThemeStat(themeStatDto)
			.build();
	}

	private record StoreInfo(
		String name,
		String phoneNumber,
		String address
	) {
		public static StoreInfo of(Store store) {
			return new StoreInfo(store.getName(), store.getPhoneNumber(), store.getAddress());
		}
	}
}
