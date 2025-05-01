package com.ddobang.backend.domain.party.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.MyJoinedPartySummaryResponse;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.types.PartyMemberRole;
import com.ddobang.backend.domain.party.types.PartyTodoFilter;
import com.ddobang.backend.domain.theme.entity.Theme;

public interface PartyRepositoryCustom {
	List<PartySummaryResponse> getParties(Long lastId, int size, PartySearchCondition partySearchCondition);

	List<PartySummaryResponse> getPartiesByTheme(Theme theme, Long lastId, int size);

	Page<PartySummaryResponse> findOtherMemberJoinedParties(Member member, Pageable pageable);

	Page<MyJoinedPartySummaryResponse> findMyPartyHistories(Member member, PartyMemberRole role,
		PartyTodoFilter todoFilter, Pageable pageable);
}
