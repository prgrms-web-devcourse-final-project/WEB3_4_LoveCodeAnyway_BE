package com.ddobang.backend.domain.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.store.dto.StoreResponse;
import com.ddobang.backend.domain.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

	/*
	관리자 전용 매장 목록 검색
	 */
	@Query("""
		SELECT new com.ddobang.backend.domain.store.dto.StoreResponse(s.id,s.name,s.address,s.status)
		FROM Store s
		WHERE s.status != 'DELETED' AND (
			LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
		order by s.name asc
		""")
	List<StoreResponse> findStoresByKeyword(@Param("keyword") String keyword);
}
