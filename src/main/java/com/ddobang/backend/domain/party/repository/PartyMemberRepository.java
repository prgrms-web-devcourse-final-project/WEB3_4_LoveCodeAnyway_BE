package com.ddobang.backend.domain.party.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddobang.backend.domain.party.entity.PartyMember;

@Repository
public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
}
