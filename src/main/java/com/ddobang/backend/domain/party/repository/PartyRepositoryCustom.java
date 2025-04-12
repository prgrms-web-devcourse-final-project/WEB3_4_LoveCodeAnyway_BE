package com.ddobang.backend.domain.party.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;

public interface PartyRepositoryCustom {
	List<PartySummaryResponse> getParties(Long lastId, int size, PartySearchCondition partySearchCondition);

	Page<PartySummaryResponse> findByMemberJoined(Member member, Pageable pageable, boolean myList);
}
