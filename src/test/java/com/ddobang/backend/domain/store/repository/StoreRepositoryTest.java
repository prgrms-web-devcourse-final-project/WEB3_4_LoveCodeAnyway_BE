package com.ddobang.backend.domain.store.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.domain.region.repository.RegionRepository;
import com.ddobang.backend.domain.store.dto.StoreResponse;
import com.ddobang.backend.domain.store.entity.Store;
import com.ddobang.backend.global.config.QuerydslConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * StoreRepositoryTest
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@Transactional
public class StoreRepositoryTest {

	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private RegionRepository regionRepository;

	private final Region region1 = Region.builder().majorRegion("서울").subRegion("홍대").build();
	private final Region region2 = Region.builder().majorRegion("서울").subRegion("강남").build();

	private final Store store1 = Store.builder()
		.name("매장1")
		.address("서울시 마포구")
		.phoneNumber("1234-1234")
		.status(Store.Status.OPENED)
		.region(region1)
		.build();
	private final Store store2 = Store.builder()
		.name("매장2")
		.address("서울시 강남구")
		.phoneNumber("5678-5678")
		.status(Store.Status.CLOSED)
		.region(region2)
		.build();

	@PersistenceContext
	private EntityManager em;

	@BeforeEach
	public void setUp() {
		em.createNativeQuery("ALTER TABLE store ALTER COLUMN id RESTART WITH 1").executeUpdate();

		regionRepository.save(region1);
		regionRepository.save(region2);

		storeRepository.save(store1);
		storeRepository.save(store2);
	}

	@Test
	@DisplayName("매장 ID로 매장 조회 성공 테스트")
	void findByIdTest() {
		// given
		Long id = 1L;

		// when
		Store store = storeRepository.findById(id).get();

		// then
		assertThat(store.getName()).isEqualTo(store1.getName());
		assertThat(store.getAddress()).isEqualTo(store1.getAddress());
		assertThat(store.getPhoneNumber()).isEqualTo(store1.getPhoneNumber());
		assertThat(store.getStatus()).isEqualTo(store1.getStatus());
		assertThat(store.getRegion().getMajorRegion()).isEqualTo(region1.getMajorRegion());
		assertThat(store.getRegion().getSubRegion()).isEqualTo(region1.getSubRegion());
	}

	@Test
	@DisplayName("매장 ID로 매장 조회 실패 테스트")
	void findByIdFailTest() {
		// given
		Long id = 3L;

		// when
		Optional<Store> store = storeRepository.findById(id);

		// then
		assertThat(store.isPresent()).isFalse();
	}

	@Test
	@DisplayName("매장 이름 키워드로 조회 - 성공 케이스")
	void findStoresByKeywordTest() {
		// given
		String keyword = "매장";

		// when
		List<StoreResponse> result = storeRepository.findStoresByKeyword(keyword);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).name()).isEqualTo(store1.getName());
		assertThat(result.get(1).name()).isEqualTo(store2.getName());
	}

	@Test
	@DisplayName("매장 이름 키워드로 조회 - 필터링 확인 (삭제 상태 제외)")
	void findStoresByKeywordExcludesDeleted() {
		// given
		Store deletedStore = Store.builder()
			.name("삭제된매장")
			.address("서울시 서초구")
			.phoneNumber("0000-0000")
			.status(Store.Status.DELETED)
			.region(region1)
			.build();
		storeRepository.save(deletedStore);
		String keyword = "매장";

		// when
		List<StoreResponse> result = storeRepository.findStoresByKeyword(keyword);

		// then
		assertThat(result).hasSize(2);
		assertThat(result).noneMatch(store -> store.name().contains("삭제"));
	}

	@Test
	@DisplayName("매장 이름 키워드로 조회 - 키워드 불일치")
	void findStoresByKeywordNoMatch() {
		// given
		String keyword = "없는 매장";

		// when
		List<StoreResponse> result = storeRepository.findStoresByKeyword(keyword);

		// then
		assertThat(result).isEmpty();
	}
}
