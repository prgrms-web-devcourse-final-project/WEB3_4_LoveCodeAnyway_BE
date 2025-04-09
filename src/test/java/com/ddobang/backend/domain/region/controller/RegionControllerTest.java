package com.ddobang.backend.domain.region.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ddobang.backend.domain.region.dto.SubRegionsResponse;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.exception.RegionErrorCode;
import com.ddobang.backend.domain.region.exception.RegionException;
import com.ddobang.backend.domain.region.service.RegionService;
import com.ddobang.backend.global.security.TestSecurityConfig;

/**
 * RegionControllerTest
 * @author 100minha
 */
@WebMvcTest(RegionController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class RegionControllerTest {

	@MockitoBean
	private RegionService regionService;

	@Autowired
	private MockMvc mockMvc;

	private final String majorRegion = "서울";
	private final Region region1 = Region.builder().majorRegion(majorRegion).subRegion("홍대").build();
	private final Region region2 = Region.builder().majorRegion(majorRegion).subRegion("강남").build();

	@Test
	@DisplayName("지역 대분류로 지역 목록 조회 성공 테스트")
	public void findByMajorRegionTest() throws Exception {
		// given
		SubRegionsResponse response1 = SubRegionsResponse.of(region1);
		SubRegionsResponse response2 = SubRegionsResponse.of(region2);
		when(regionService.getSubRegionsByMajorRegion(majorRegion))
			.thenReturn(List.of(response1, response2));

		// when
		ResultActions result = mockMvc.perform(get("/api/v1/regions")
			.param("majorRegion", majorRegion)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result
			.andExpect(handler().handlerType(RegionController.class))
			.andExpect(handler().methodName("getSubRegionsByMajorRegion"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].subRegion").value("홍대"))
			.andExpect(jsonPath("$.data[1].subRegion").value("강남"));
	}

	@Test
	@DisplayName("지역 대분류로 지역 목록 조회 실패 테스트")
	public void findByMajorRegionFailTest() throws Exception {
		// given
		doThrow(new RegionException(RegionErrorCode.REGION_NOT_FOUND))
			.when(regionService).getSubRegionsByMajorRegion(anyString());

		// when
		ResultActions result = mockMvc.perform(get("/api/v1/regions")
			.param("majorRegion", "")
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result
			.andExpect(handler().handlerType(RegionController.class))
			.andExpect(handler().methodName("getSubRegionsByMajorRegion"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("지역 정보를 찾을 수 없습니다."));
	}
}
