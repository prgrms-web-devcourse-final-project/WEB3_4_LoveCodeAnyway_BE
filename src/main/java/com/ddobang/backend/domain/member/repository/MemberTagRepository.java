package com.ddobang.backend.domain.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ddobang.backend.domain.member.dto.response.MemberTagResponse;
import com.ddobang.backend.domain.member.entity.MemberTag;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {

	@Query("""
		    SELECT new com.ddobang.backend.domain.member.dto.response.MemberTagResponse(
		        t.id,
		        t.name
		    )
		    FROM MemberTag t
		""")
	List<MemberTagResponse> findAllAsDto();
}
