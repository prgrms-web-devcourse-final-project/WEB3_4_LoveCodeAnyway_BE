package com.ddobang.backend.domain.store.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.dto.StoreRequest;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.service.StoreService;
import com.ddobang.backend.global.exception.GlobalErrorCode;
import com.ddobang.backend.global.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AdminStoreControllerTest
 * 관리자 전용 StoreController 테스트 클래스
 * @author 100minha
 */
@WebMvcTest(AdminStoreController.class)
@Import({SecurityConfig.class})
public class AdminStoreControllerTest {

	@MockitoBean
	private StoreService storeService;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private final Region region1 = Region.builder().majorRegion("서울").subRegion("홍대").build();
	private final Region region2 = Region.builder().majorRegion("서울").subRegion("강남").build();

	private final StoreRequest storeRequest = StoreRequest.builder()
		.name("매장1")
		.address("서울시 마포구")
		.phoneNumber("1234-1234")
		.siteUrl("https://store1.com")
		.status(Store.Status.OPEN)
		.regionId(1L)
		.build();
	private final Store store = Store.builder()
		.name("매장2")
		.address("서울시 강남구")
		.phoneNumber("5678-5678")
		.siteUrl("https://store2.com")
		.status(Store.Status.CLOSE)
		.region(region2)
		.build();

	@Test
	@DisplayName("매장 등록 성공 테스트")
	public void saveForAdminTest() throws Exception {
		// given
		doNothing().when(storeService).saveForAdmin(storeRequest);

		// when
		ResultActions result = mockMvc.perform(
			post("/admin/stores")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(storeRequest)));

		result
			.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("매장 등록에 성공했습니다."));
	}

	@Test
	@DisplayName("매장 등록 실패(매장명 공백) 테스트")
	public void saveForAdminFailWhenBlankNameTest() throws Exception {
		// given
		StoreRequest blankNameRequest = StoreRequest.builder()
			.name("")
			.address("서울시 마포구")
			.phoneNumber("1234-1234")
			.siteUrl("https://store1.com")
			.status(Store.Status.OPEN)
			.regionId(1L)
			.build();
		doNothing().when(storeService).saveForAdmin(any(StoreRequest.class));

		// when
		ResultActions result = mockMvc.perform(
			post("/admin/stores")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(blankNameRequest)));
		GlobalErrorCode errorCode = GlobalErrorCode.NOT_VALID;

		result
			.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(errorCode.getErrorCode()))
			.andExpect(jsonPath("$.message").value(errorCode.getMessage()));
	}
}
