package com.ddobang.backend.domain.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.service.RegionService;
import com.ddobang.backend.domain.store.dto.StoreRequest;
import com.ddobang.backend.domain.store.dto.StoreResponse;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.domain.store.exception.StoreErrorCode;
import com.ddobang.backend.domain.store.exception.StoreException;
import com.ddobang.backend.domain.store.repository.StoreRepository;

/**
 * StoreServiceTest
 * @author 100minha
 */
@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

	@InjectMocks
	private StoreService storeService;

	@Mock
	private StoreRepository storeRepository;
	@Mock
	private RegionService regionService;

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
	@DisplayName("관리자 전용 매장 저장 테스트")
	void saveForAdminTest() {
		// given
		ArgumentCaptor<Store> storeCaptor = ArgumentCaptor.forClass(Store.class);
		when(regionService.findById(anyLong())).thenReturn(region1);

		// when
		storeService.saveForAdmin(storeRequest);

		// then
		verify(storeRepository).save(storeCaptor.capture());
		Store savedStore = storeCaptor.getValue();

		assertThat(savedStore.getName()).isEqualTo(storeRequest.name());
		assertThat(savedStore.getAddress()).isEqualTo(storeRequest.address());
		assertThat(savedStore.getPhoneNumber()).isEqualTo(storeRequest.phoneNumber());
		assertThat(savedStore.getStatus()).isEqualTo(Store.Status.valueOf(storeRequest.status()));
		assertThat(savedStore.getRegion()).isEqualTo(region1);
	}

	@Test
	@DisplayName("매장 ID로 매장 조회 성공 테스트")
	void findByIdTest() {
		// given
		Long id = 1L;
		when(storeRepository.findById(id)).thenReturn(Optional.of(store));

		// when
		Store result = storeService.findById(id);

		// then
		assertThat(result).isEqualTo(store);
	}

	@Test
	@DisplayName("매장 ID로 매장 조회 실패 테스트")
	void findByIdFailTest() {
		// given
		Long id = 3L;
		when(storeRepository.findById(id)).thenReturn(Optional.empty());

		// when
		StoreException exception = assertThrows(
			StoreException.class,
			() -> storeService.findById(3L)
		);
		StoreErrorCode errorCode = StoreErrorCode.STORE_NOT_FOUND;

		// then
		assertThat(exception.getErrorCode()).isEqualTo(errorCode);
		assertThat(exception.getErrorCode().getErrorCode()).isEqualTo(errorCode.getErrorCode());
		assertThat(exception.getErrorCode().getStatus()).isEqualTo(errorCode.getStatus());
		assertThat(exception.getErrorCode().getMessage()).isEqualTo(errorCode.getMessage());
	}

	@Test
	@DisplayName("매장 수정 성공 테스트")
	void modifyTest() {
		// given
		Long id = 1L;
		when(storeRepository.findById(id)).thenReturn(Optional.of(store));
		when(regionService.findById(anyLong())).thenReturn(region1);

		// when
		storeService.modify(id, storeRequest);

		// then
		assertThat(store.getName()).isEqualTo(storeRequest.name());
		assertThat(store.getAddress()).isEqualTo(storeRequest.address());
		assertThat(store.getPhoneNumber()).isEqualTo(storeRequest.phoneNumber());
		assertThat(store.getStatus()).isEqualTo(Store.Status.valueOf(storeRequest.status()));
		assertThat(store.getRegion()).isEqualTo(region1);
	}

	@Test
	@DisplayName("매장 삭제 성공 테스트")
	void deleteTest() {
		// given
		Long id = 1L;
		when(storeRepository.findById(id)).thenReturn(Optional.of(store));

		// when
		storeService.delete(id);

		// then
		assertThat(store.getStatus()).isEqualTo(Store.Status.DELETED);
	}

	@Test
	@DisplayName("매장 검색 테스트")
	void getStoresByKeywordTest() {
		// given
		StoreResponse storeResponse = new StoreResponse(1L, "매장1", "서울시 마포구", Store.Status.OPENED);

		String keyword = "매장";
		when(storeRepository.findStoresByKeyword(keyword)).thenReturn(List.of(storeResponse));

		// when
		List<StoreResponse> result = storeService.getStoresByKeyword(keyword);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(storeResponse);
	}
}
