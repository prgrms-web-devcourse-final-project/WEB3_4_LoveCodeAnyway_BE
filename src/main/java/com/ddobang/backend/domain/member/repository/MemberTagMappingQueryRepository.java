package com.ddobang.backend.domain.member.repository;

import java.util.List;

import com.ddobang.backend.domain.member.entity.MemberTagMapping;

public interface MemberTagMappingQueryRepository {
	List<MemberTagMapping> findAllByMemberId(Long memberId);
}
