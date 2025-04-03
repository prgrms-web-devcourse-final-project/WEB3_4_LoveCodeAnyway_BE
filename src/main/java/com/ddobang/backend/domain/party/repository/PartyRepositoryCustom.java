package com.ddobang.backend.domain.party.repository;

import java.util.List;

import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.entity.Party;

public interface PartyRepositoryCustom {
	List<Party> getParties(Long lastId, int size, PartySearchCondition partySearchCondition);
}
