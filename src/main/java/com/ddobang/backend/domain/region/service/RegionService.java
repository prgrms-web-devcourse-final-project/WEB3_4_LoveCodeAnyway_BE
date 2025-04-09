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

	public List<SubRegionsResponse> getSubRegionsByMajorRegion(String majorRegion) {

		List<SubRegionsResponse> subRegions = regionRepository.findSubRegionsByMajorRegion(majorRegion);
		if (subRegions.isEmpty()) {
			throw new RegionException(RegionErrorCode.REGION_NOT_FOUND);
		}

		return subRegions;
	}

	public Region findById(Long id) {

		return regionRepository.findById(id)
			.orElseThrow(() -> new RegionException(RegionErrorCode.REGION_NOT_FOUND));
	}
}
