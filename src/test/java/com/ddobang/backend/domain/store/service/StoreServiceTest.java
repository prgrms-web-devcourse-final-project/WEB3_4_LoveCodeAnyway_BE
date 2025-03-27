package com.ddobang.backend.domain.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.ddobang.backend.domain.store.entity.Store;
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
	@DisplayName("관리자 전용 매장 저장 성공 테스트")
	public void saveForAdminTest() {
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
		assertThat(savedStore.getSiteUrl()).isEqualTo(storeRequest.siteUrl());
		assertThat(savedStore.getStatus()).isEqualTo(storeRequest.status());
		assertThat(savedStore.getRegion()).isEqualTo(region1);
	}
}
