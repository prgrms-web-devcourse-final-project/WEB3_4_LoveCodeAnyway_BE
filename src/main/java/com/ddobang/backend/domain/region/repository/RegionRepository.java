package com.ddobang.backend.domain.region.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.region.dto.SubRegionsResponse;
import com.ddobang.backend.domain.region.entity.Region;

/**
 * RegionRepository
 * @author 100minha
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

	@Query("""
		SELECT new com.ddobang.backend.domain.region.dto.SubRegionsResponse(r.id,r.subRegion)
		FROM Region r
		WHERE r.majorRegion = :majorRegion
		""")
	List<SubRegionsResponse> findSubRegionsByMajorRegion(String majorRegion);

}
