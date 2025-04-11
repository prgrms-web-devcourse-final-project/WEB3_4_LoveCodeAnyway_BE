package com.ddobang.backend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddobang.backend.domain.member.entity.MemberTagMapping;
import com.ddobang.backend.domain.member.entity.id.MemberTagMappingId;

public interface MemberTagMappingRepository extends JpaRepository<MemberTagMapping, MemberTagMappingId> {
}
