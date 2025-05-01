package com.ddobang.backend.domain.theme.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.theme.entity.Theme;

import lombok.Builder;

/**
 * ThemeForAdminResponse
 * 관리자 전용 테마 상세 조회 응답 DTO
 * @author 100minha
 */
@Builder
public record ThemeForAdminResponse(
	Long Id,
	String name,
	String description,
	int runtime,
	float officialDifficulty,
	int price,
	String recommendedParticipants,
	String thumbnailUrl,
	String reservationUrl,
	List<String> tags,
	StoreInfo storeInfo
) {
	public static ThemeForAdminResponse of(Theme theme) {
		return ThemeForAdminResponse.builder()
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
			.build();
	}

	@Builder
	private record StoreInfo(
		Long id,
		String name,
		String phoneNumber,
		String region,
		Store.Status status,
		String address
	) {
		public static StoreInfo of(Store store) {
			return StoreInfo.builder()
				.id(store.getId())
				.name(store.getName())
				.phoneNumber(store.getPhoneNumber())
				.region(store.getRegion().getMajorRegion() + " " + store.getRegion().getSubRegion())
				.status(store.getStatus())
				.address(store.getAddress())
				.build();
		}
	}
}
