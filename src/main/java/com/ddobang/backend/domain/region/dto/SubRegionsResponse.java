package com.ddobang.backend.domain.region.dto;

import com.ddobang.backend.domain.region.entity.Region;

/**
 * SubRegionsResponse
 * 대분류에 해당하는 소분류 목록 응답 DTO
 * @author 100minha
 */
public record SubRegionsResponse(
	Long id,
	String subRegion) {

	public static SubRegionsResponse of(Region region) {
		return new SubRegionsResponse(region.getId(), region.getSubRegion());
	}
}
