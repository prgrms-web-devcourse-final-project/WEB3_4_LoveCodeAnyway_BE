package com.ddobang.backend.domain.store.controller;

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

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.dto.StoreRequest;
import com.ddobang.backend.domain.store.dto.StoreResponse;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.exception.StoreErrorCode;
import com.ddobang.backend.domain.store.exception.StoreException;
import com.ddobang.backend.domain.store.service.StoreService;
import com.ddobang.backend.global.exception.GlobalErrorCode;
import com.ddobang.backend.global.security.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AdminStoreControllerTest
 * 관리자 전용 StoreController 테스트 클래스
 * @author 100minha
 */
@WebMvcTest(AdminStoreController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
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
		.status("OPENED")
		.regionId(1L)
		.build();
	private final Store store = Store.builder()
		.name("매장2")
		.address("서울시 강남구")
		.phoneNumber("5678-5678")
		.status(Store.Status.CLOSED)
		.region(region2)
		.build();

	@Test
	@DisplayName("매장 등록 성공 테스트")
	void saveForAdminTest() throws Exception {
		// given
		// when
		ResultActions result = mockMvc.perform(post("/api/v1/admin/stores").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(storeRequest)));

		result.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("매장 등록에 성공했습니다."));
	}

	@Test
	@DisplayName("매장 등록 실패(매장명 공백) 테스트")
	void saveForAdminFailWhenBlankNameTest() throws Exception {
		// given
		StoreRequest blankNameRequest = StoreRequest.builder()
			.name("")
			.address("서울시 마포구")
			.phoneNumber("1234-1234")
			.status("OPENED")
			.regionId(1L)
			.build();

		// when
		ResultActions result = mockMvc.perform(post("/api/v1/admin/stores").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(blankNameRequest)));
		GlobalErrorCode errorCode = GlobalErrorCode.NOT_VALID;

		result.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(errorCode.getErrorCode()))
			.andExpect(jsonPath("$.message").value(errorCode.getMessage()));
	}

	@Test
	@DisplayName("매장 등록 실패(유효 하지 않은 status) 테스트")
	void saveForAdminFailWhenInvalidStatusTest() throws Exception {
		// given
		StoreRequest invalidStatusRequest = StoreRequest.builder()
			.name("매장1")
			.address("서울시 마포구")
			.phoneNumber("1234-1234")
			.status("INVALID")
			.regionId(1L)
			.build();

		// when
		ResultActions result = mockMvc.perform(post("/api/v1/admin/stores").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(invalidStatusRequest))
			.contentType(MediaType.APPLICATION_JSON));
		GlobalErrorCode errorCode = GlobalErrorCode.NOT_VALID;

		result.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(errorCode.getErrorCode()))
			.andExpect(jsonPath("$.message").value(errorCode.getMessage()))
			.andExpect(jsonPath("$.errors[0].field").value("status"))
			.andExpect(jsonPath("$.errors[0].message").value("유효하지 않은 status 입력입니다."))
		;
	}

	@Test
	@DisplayName("매장 수정 성공 테스트")
	void modifyTest() throws Exception {
		// given
		Long id = 1L;

		// when
		ResultActions result = mockMvc.perform(put("/api/v1/admin/stores/" + id)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(storeRequest)));

		// then
		result.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(id + "번 매장 수정에 성공했습니다."));
	}

	@Test
	@DisplayName("매장 수정 실패(존재하지 않는 id) 테스트")
	void modifyFailWhenNotExistTest() throws Exception {
		// given
		Long id = 999L;
		doThrow(new StoreException(StoreErrorCode.STORE_NOT_FOUND))
			.when(storeService).modify(id, storeRequest);

		// when
		ResultActions result = mockMvc.perform(put("/api/v1/admin/stores/" + id)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(storeRequest)));
		StoreErrorCode errorCode = StoreErrorCode.STORE_NOT_FOUND;

		// then
		result.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.errorCode").value(errorCode.getErrorCode()))
			.andExpect(jsonPath("$.message").value(errorCode.getMessage()));
	}

	@Test
	@DisplayName("매장 삭제 성공 테스트")
	void deleteTest() throws Exception {
		// given
		Long id = 1L;

		// when
		ResultActions result = mockMvc.perform(delete("/api/v1/admin/stores/" + id)
			.contentType(MediaType.APPLICATION_JSON));

		//then
		result.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(id + "번 매장 삭제에 성공했습니다."));
	}

	@Test
	@DisplayName("매장 검색 성공 테스트")
	void getStoresByKeywordTest() throws Exception {
		// given
		String keyword = "매장";
		StoreResponse storeResponse1 = new StoreResponse(1L, "매장1", "서울시 마포구", Store.Status.OPENED);
		StoreResponse storeResponse2 = new StoreResponse(2L, "매장2", "서울시 강서구", Store.Status.INACTIVE);
		when(storeService.getStoresByKeyword(keyword)).thenReturn(List.of(storeResponse1, storeResponse2));

		// when
		ResultActions result = mockMvc.perform(get("/api/v1/admin/stores")
			.param("keyword", keyword)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andExpect(handler().handlerType(AdminStoreController.class))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[0].name").value(storeResponse1.name()))
			.andExpect(jsonPath("$.data[1].status").value(storeResponse2.status().name()));

	}
}
