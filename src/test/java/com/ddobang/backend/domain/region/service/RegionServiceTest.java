package com.ddobang.backend.domain.region.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
		SubRegionsResponse response1 = SubRegionsResponse.of(region1);
		SubRegionsResponse response2 = SubRegionsResponse.of(region2);
		List<SubRegionsResponse> responses = List.of(response1, response2);
		when(regionRepository.findSubRegionsByMajorRegion(majorRegion)).thenReturn(responses);

		// when
		List<SubRegionsResponse> result = regionService.getSubRegionsByMajorRegion(majorRegion);

		// then
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).subRegion()).isEqualTo("홍대");
		assertThat(result.get(1).subRegion()).isEqualTo("강남");
	}

	@Test
	@DisplayName("지역 대분류로 지역 목록 조회 실패 테스트")
	public void findByMajorRegionFailTest() {
		// given
		when(regionRepository.findSubRegionsByMajorRegion(anyString())).thenReturn(Collections.emptyList());

		// when
		RegionException exception = assertThrows(
			RegionException.class,
			() -> regionService.getSubRegionsByMajorRegion("NOT_EXIST")
		);
		RegionErrorCode errorCode = RegionErrorCode.REGION_NOT_FOUND;

		// then
		assertThat(exception.getErrorCode()).isEqualTo(RegionErrorCode.REGION_NOT_FOUND);
		assertThat(exception.getErrorCode().getErrorCode()).isEqualTo("REGION_001");
		assertThat(exception.getErrorCode().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("지역 정보를 찾을 수 없습니다.");
		assertThat(exception.getErrorCode()).isEqualTo(errorCode);
	}

	@Test
	@DisplayName("id로 지역 조회 성공 테스트")
	public void findByIdTest() {
		// given
		when(regionRepository.findById(anyLong())).thenReturn(Optional.of(region1));

		// when
		Region region = regionService.findById(1L);

		// then
		assertThat(region.getMajorRegion()).isEqualTo(majorRegion);
		assertThat(region.getSubRegion()).isEqualTo("홍대");
	}

	@Test
	@DisplayName("id로 지역 조회 실패 테스트")
	public void findByIdFailTest() {
		// given
		when(regionRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when
		RegionException exception = assertThrows(
			RegionException.class,
			() -> regionService.findById(3L)
		);
		RegionErrorCode errorCode = RegionErrorCode.REGION_NOT_FOUND;

		// then
		assertThat(exception.getErrorCode()).isEqualTo(errorCode);
		assertThat(exception.getErrorCode().getErrorCode()).isEqualTo(errorCode.getErrorCode());
		assertThat(exception.getErrorCode().getStatus()).isEqualTo(errorCode.getStatus());
		assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
	}
}
