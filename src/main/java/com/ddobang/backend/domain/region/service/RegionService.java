package com.ddobang.backend.domain.region.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.region.dto.SubRegionsResponse;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.exception.RegionErrorCode;
import com.ddobang.backend.domain.region.exception.RegionException;
import com.ddobang.backend.domain.region.repository.RegionRepository;

import lombok.RequiredArgsConstructor;

/**
 * RegionService
 * @author 100minha
 */
@Service
@RequiredArgsConstructor
public class RegionService {

	private final RegionRepository regionRepository;

	public List<Region> findByMajorRegion(String majorRegion) {

		List<Region> regions = regionRepository.findByMajorRegion(majorRegion);
		if (regions.isEmpty()) {
			throw new RegionException(RegionErrorCode.REGION_NOT_FOUND);
		}

		return regions;
	}

	public List<SubRegionsResponse> getSubRegionsByRegions(List<Region> regions) {

		return regions.stream()
			.map(SubRegionsResponse::of)
			.toList();
	}
}
