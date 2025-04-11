package com.ddobang.backend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddobang.backend.domain.member.entity.MemberTag;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
}
