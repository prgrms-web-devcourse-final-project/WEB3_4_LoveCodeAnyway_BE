package com.ddobang.backend.domain.store.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.store.entity.Store;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * StoreRepositoryTest
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class StoreRepositoryTest {

	@Autowired
	private StoreRepository storeRepository;

	private final Region region1 = Region.builder().majorRegion("서울").subRegion("홍대").build();
	private final Region region2 = Region.builder().majorRegion("서울").subRegion("강남").build();

	private final Store store1 = Store.builder()
		.name("매장1")
		.address("서울시 마포구")
		.phoneNumber("1234-1234")
		.siteUrl("https://store1.com")
		.status(Store.Status.OPEN)
		.region(region1)
		.build();
	private final Store store2 = Store.builder()
		.name("매장2")
		.address("서울시 강남구")
		.phoneNumber("5678-5678")
		.siteUrl("https://store2.com")
		.status(Store.Status.CLOSE)
		.region(region2)
		.build();

	@PersistenceContext
	private EntityManager em;

	@BeforeEach
	public void setUp() {
		em.createNativeQuery("ALTER TABLE store ALTER COLUMN id RESTART WITH 1").executeUpdate();

		storeRepository.save(store1);
		storeRepository.save(store2);
	}

	@Test
	@DisplayName("매장 ID로 매장 조회 성공 테스트")
	public void findByIdTest() {
		// given
		Long id = 1L;

		// when
		Store store = storeRepository.findById(id).get();

		// then
		assertThat(store.getName()).isEqualTo(store1.getName());
		assertThat(store.getAddress()).isEqualTo(store1.getAddress());
		assertThat(store.getPhoneNumber()).isEqualTo(store1.getPhoneNumber());
		assertThat(store.getSiteUrl()).isEqualTo(store1.getSiteUrl());
		assertThat(store.getStatus()).isEqualTo(store1.getStatus());
		assertThat(store.getRegion().getMajorRegion()).isEqualTo(region1.getMajorRegion());
		assertThat(store.getRegion().getSubRegion()).isEqualTo(region1.getSubRegion());
	}

	@Test
	@DisplayName("매장 ID로 매장 조회 실패 테스트")
	public void findByIdFailTest() {
		// given
		Long id = 3L;

		// when
		Optional<Store> store = storeRepository.findById(id);

		// then
		assertThat(store.isPresent()).isFalse();
	}
}
