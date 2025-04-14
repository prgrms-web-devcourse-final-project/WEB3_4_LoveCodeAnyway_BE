package com.ddobang.backend.domain.region.controller;

import com.ddobang.backend.domain.region.dto.SubRegionsResponse;
import com.ddobang.backend.domain.region.service.RegionService;
import com.ddobang.backend.global.response.ResponseFactory;
import com.ddobang.backend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RegionController
 *
 * @author 100minha
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/regions")
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<SubRegionsResponse>>> getSubRegionsByMajorRegion(
            @RequestParam String majorRegion
    ) {
        List<SubRegionsResponse> subRegions = regionService.getSubRegionsByMajorRegion(majorRegion);
        return ResponseFactory.ok(subRegions);
    }
}
