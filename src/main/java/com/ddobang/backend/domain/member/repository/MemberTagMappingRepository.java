package com.ddobang.backend.domain.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.entity.MemberTagMapping;
import com.ddobang.backend.domain.member.entity.id.MemberTagMappingId;

public interface MemberTagMappingRepository extends JpaRepository<MemberTagMapping, MemberTagMappingId> {
	// 멤버로 매핑된 사용자 태그 조회
	List<MemberTagMapping> findAllByMember(Member member);

	// 멤버 ID로 매핑된 사용자 태그 조회
	List<MemberTagMapping> findByMemberId(Long id);

	// 멤버 ID로 매핑된 사용자 태그 삭제
	void deleteByMemberId(Long memberId);
}
