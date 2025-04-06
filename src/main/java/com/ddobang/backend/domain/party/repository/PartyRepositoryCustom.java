package com.ddobang.backend.domain.party.repository;

import java.util.List;

import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;

public interface PartyRepositoryCustom {
	List<PartySummaryResponse> getParties(Long lastId, int size, PartySearchCondition partySearchCondition);
}
