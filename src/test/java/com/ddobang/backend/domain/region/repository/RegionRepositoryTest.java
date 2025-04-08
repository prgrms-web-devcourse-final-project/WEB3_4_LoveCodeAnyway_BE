package com.ddobang.backend.domain.region.repository;

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

import com.ddobang.backend.domain.region.dto.SubRegionsResponse;
import com.ddobang.backend.domain.region.entity.Region;
import com.ddobang.backend.global.config.QuerydslConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * RegionRepositoryTest
 * @author 100minha
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@Transactional
public class RegionRepositoryTest {

	@Autowired
	private RegionRepository regionRepository;

	private final String majorRegion = "서울";
	private final Region region1 = Region.builder().majorRegion(majorRegion).subRegion("홍대").build();
	private final Region region2 = Region.builder().majorRegion(majorRegion).subRegion("강남").build();

	@PersistenceContext
	EntityManager em;

	@BeforeEach
	public void setUp() {
		em.createNativeQuery("ALTER TABLE region ALTER COLUMN id RESTART WITH 1").executeUpdate();

		regionRepository.save(region1);
		regionRepository.save(region2);
	}

	@Test
	@DisplayName("지역 대분류로 지역 소분류 목록 조회 성공 테스트")
	public void findSubRegionsByMajorRegionTest() {
		//given
		// when
		List<SubRegionsResponse> regions = regionRepository.findSubRegionsByMajorRegion(majorRegion);

		// then
		assertThat(regions.size()).isEqualTo(2);
		assertThat(regions.get(0).id()).isEqualTo(1L);
		assertThat(regions.get(0).subRegion()).isEqualTo(region1.getSubRegion());
		assertThat(regions.get(1).id()).isEqualTo(2L);
		assertThat(regions.get(1).subRegion()).isEqualTo(region2.getSubRegion());
	}

	@Test
	@DisplayName("지역 대분류로 지역 목록 조회 실패(공백) 테스트")
	public void findByMajorRegionFailWhenBlankTest() {
		//given
		String blankMajorRegion = "";

		// when
		List<SubRegionsResponse> regions = regionRepository.findSubRegionsByMajorRegion(blankMajorRegion);

		// then
		assertThat(regions.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("지역 대분류로 지역 목록 조회 실패(데이터에 없는 지역) 테스트")
	public void findByMajorRegionFailWhenNotExistTest() {
		//given
		String notExistmajorRegion = "경기/인천";

		// when
		List<SubRegionsResponse> regions = regionRepository.findSubRegionsByMajorRegion(notExistmajorRegion);

		// then
		assertThat(regions.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("id로 지역 조회 성공 테스트")
	public void findByIdTest() {
		//given
		Long id = 1L;

		// when
		Region region = regionRepository.findById(id).orElse(null);

		// then
		assertThat(region).isNotNull();
		assertThat(region.getId()).isEqualTo(id);
		assertThat(region.getMajorRegion()).isEqualTo(majorRegion);
		assertThat(region.getSubRegion()).isEqualTo(region1.getSubRegion());
	}

	@Test
	@DisplayName("데이터에 없는 id로 지역 조회 테스트")
	public void findByIdFailWhenNotExistTest() {
		//given
		Long notExistId = 3L;

		// when
		Optional<Region> region = regionRepository.findById(notExistId);

		// then
		assertThat(region.isPresent()).isFalse();
	}
}
