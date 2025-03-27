package com.ddobang.backend.domain.region.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.region.entity.Region;

/**
 * RegionRepository
 * @author 100minha
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

	List<Region> findByMajorRegion(String majorRegion);
}
