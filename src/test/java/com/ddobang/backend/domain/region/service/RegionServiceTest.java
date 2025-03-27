package com.ddobang.backend.domain.region.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.ddobang.backend.domain.region.dto.SubRegionsResponse;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.exception.RegionErrorCode;
import com.ddobang.backend.domain.region.exception.RegionException;
import com.ddobang.backend.domain.region.repository.RegionRepository;

/**
 * RegionServiceTest
 * @author 100minha
 */
@ExtendWith(MockitoExtension.class)
public class RegionServiceTest {

	@InjectMocks
	private RegionService regionService;

	@Mock
	private RegionRepository regionRepository;

	private final String majorRegion = "서울";
	private final Region region1 = Region.builder().majorRegion(majorRegion).subRegion("홍대").build();
	private final Region region2 = Region.builder().majorRegion(majorRegion).subRegion("강남").build();

	@Test
	@DisplayName("지역 대분류로 지역 목록 조회 성공 테스트")
	public void findByMajorRegionTest() {
		// given
		List<Region> regions = List.of(region1, region2);
		when(regionRepository.findByMajorRegion(majorRegion)).thenReturn(regions);

		// when
		List<Region> result = regionService.findByMajorRegion(majorRegion);

		// then
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getMajorRegion()).isEqualTo(majorRegion);
		assertThat(result.get(0).getSubRegion()).isEqualTo("홍대");
		assertThat(result.get(1).getSubRegion()).isEqualTo("강남");
	}

	@Test
	@DisplayName("지역 대분류로 지역 목록 조회 실패 테스트")
	public void findByMajorRegionFailTest() {
		// given
		when(regionRepository.findByMajorRegion(anyString())).thenReturn(Collections.emptyList());

		// when
		RegionException exception = assertThrows(
			RegionException.class,
			() -> regionService.findByMajorRegion("NOT_EXIST_REGION")
		);

		// then
		assertThat(exception.getErrorCode()).isEqualTo(RegionErrorCode.REGION_NOT_FOUND);
		assertThat(exception.getErrorCode().getErrorCode()).isEqualTo("REGION_001");
		assertThat(exception.getErrorCode().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("지역 정보를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("List<Region>에서 List<SubRegionsResponse>로 변환 성공 테스트")
	public void getSubRegionsByRegionsTest() {
		// given
		List<Region> regions = List.of(region1, region2);

		// when
		List<SubRegionsResponse> result = regionService.getSubRegionsByRegions(regions);

		// then
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).subRegion()).isEqualTo("홍대");
		assertThat(result.get(1).subRegion()).isEqualTo("강남");
	}

	@Test
	@DisplayName("EmptyList에서 List<SubRegionsResponse>로 변환 테스트")
	public void getSubRegionsByRegionsWhenEmptyTest() {
		// given
		List<Region> regions = Collections.emptyList();

		// when
		List<SubRegionsResponse> result = regionService.getSubRegionsByRegions(regions);

		// then
		assertThat(result.isEmpty()).isTrue();
	}

}
