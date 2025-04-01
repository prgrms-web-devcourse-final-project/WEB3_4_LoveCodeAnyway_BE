package com.ddobang.backend.domain.party.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.party.dto.PartyDto;
import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartyDetailResponse;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.exception.PartyErrorCode;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.repository.PartyRepository;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.response.SliceDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyService {
	private final PartyRepository partyRepository;
	private final ThemeService themeService;
	private final MemberService memberService;
	private final PartyValidationService partyValidationService;

	public SliceDto<PartySummaryResponse> getParties(Long lastId, int size, PartySearchCondition partySearchCondition) {
		List<Party> parties = partyRepository.getParties(lastId, size + 1, partySearchCondition);

		List<PartySummaryResponse> content = parties.stream()
			.map(PartySummaryResponse::from)
			.toList();

		return SliceDto.of(content, size);
	}

	public Party getPartyById(Long id) {
		return partyRepository.findById(id)
			.orElseThrow(() -> new PartyException(PartyErrorCode.PARTY_NOT_FOUND));
	}

	public PartyDetailResponse getPartyDetailResponse(Long id, Member actor) {
		return PartyDetailResponse.from(getPartyById(id), actor);
	}

	public PartyDto createParty(PartyRequest request, Member actor) {
		Theme theme = themeService.getById(request.themeId());
		return PartyDto.toDto(Party.of(request, theme, actor));
	}

	public PartyDto modifyParty(Long id, PartyRequest request, Member actor) {
		Party party = getPartyById(id);
		partyValidationService.checkHost(party, actor);
		Theme theme = themeService.getById(request.themeId());
		party.modifyParty(request, theme);
		return PartyDto.toDto(party);
	}

	public void softDeleteParty(Long id, Member actor) {
		Party party = getPartyById(id);
		partyValidationService.checkHost(party, actor);
		party.delete();
	}

	public void applyParty(Long id, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.checkRecruiting(party);
		partyValidationService.validateApply(party, actor);

		if (party.isPartyMember(actor)) {
			party.updatePartyMemberStatus(actor, PartyMemberStatus.APPLICANT);
		} else {
			party.addPartyMember(actor);
		}
	}

	public void cancelAppliedParty(Long id, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.checkOpen(party);
		partyValidationService.validateCancel(party, actor);

		party.updatePartyMemberStatus(actor, PartyMemberStatus.CANCELLED);
		party.updatePartyStatus();
	}

	public void acceptPartyMember(Long id, Long memberId, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.checkRecruiting(party);
		partyValidationService.checkHost(party, actor);

		Member member = memberService.getMemberById(memberId);
		partyValidationService.validateAccept(party, member);

		party.updatePartyMemberStatus(actor, PartyMemberStatus.ACCEPTED);
		party.updatePartyStatus();
	}

	public void executeParty(Long id, Member actor) {
		Party party = getPartyById(id);
		partyValidationService.checkHost(party, actor);
		partyValidationService.checkExecutable(party);

		party.updateFinalStatus(PartyStatus.COMPLETED);

	}

	public void unexecuteParty(Long id, Member actor) {
		Party party = getPartyById(id);
		partyValidationService.checkHost(party, actor);
		partyValidationService.checkExecutable(party);

		party.updateFinalStatus(PartyStatus.CANCELLED);
	}
}
