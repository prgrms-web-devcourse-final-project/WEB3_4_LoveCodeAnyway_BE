package com.ddobang.backend.domain.region.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ddobang.backend.domain.region.dto.SubRegionsResponse;
import com.ddobang.backend.domain.region.service.RegionService;

import lombok.RequiredArgsConstructor;

/**
 * RegionController
 * @author 100minha
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/regions")
public class RegionController {

	private final RegionService regionService;

	@GetMapping
	public List<SubRegionsResponse> getSubRegionsByMajorRegion(
		@RequestParam String majorRegion
	) {

		return regionService.getSubRegionsByRegions(
			regionService.findByMajorRegion(majorRegion)
		);
	}
}
