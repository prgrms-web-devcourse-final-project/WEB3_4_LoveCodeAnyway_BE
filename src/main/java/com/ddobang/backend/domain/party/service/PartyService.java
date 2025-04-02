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
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.exception.ThemeErrorCode;
import com.ddobang.backend.domain.theme.exception.ThemeException;
import com.ddobang.backend.domain.theme.repository.ThemeStatRepository;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.response.SliceDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyService {
	private final PartyRepository partyRepository;
	private final ThemeService themeService;
	private final MemberService memberService;
	private final PartyValidationService partyValidationService;
	private final ThemeStatRepository themeStatRepository;

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
		Party party = getPartyById(id);
		ThemeStat themeStat = themeStatRepository.findById(party.getTheme().getId())
			.orElseThrow(() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND));
		return PartyDetailResponse.from(party, themeStat, actor);
	}

	@Transactional
	public PartyDto createParty(PartyRequest request, Member actor) {
		Theme theme = themeService.getThemeById(request.themeId());
		return PartyDto.toDto(Party.of(request, theme, actor));
	}

	@Transactional
	public PartyDto modifyParty(Long id, PartyRequest request, Member actor) {
		Party party = getPartyById(id);
		partyValidationService.validateModifiable(party, actor);
		Theme theme = themeService.getThemeById(request.themeId());
		party.modifyParty(request, theme);
		return PartyDto.toDto(party);
	}

	@Transactional
	public void softDeleteParty(Long id, Member actor) {
		Party party = getPartyById(id);
		partyValidationService.checkHost(party, actor);
		party.delete();
	}

	@Transactional
	public void applyParty(Long id, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.validateApply(party, actor);

		if (party.isPartyMember(actor)) {
			party.updatePartyMemberStatus(actor, PartyMemberStatus.APPLICANT);
		} else {
			party.addPartyMember(actor);
		}
	}

	@Transactional
	public void cancelAppliedParty(Long id, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.validateCancel(party, actor);

		party.updatePartyMemberStatus(actor, PartyMemberStatus.CANCELLED);
		party.updatePartyStatus();
	}

	@Transactional
	public void acceptPartyMember(Long id, Long memberId, Member actor) {
		Party party = getPartyById(id);
		Member member = memberService.getMemberById(memberId);

		partyValidationService.validateAccept(party, member, actor);

		party.updatePartyMemberStatus(actor, PartyMemberStatus.ACCEPTED);
		party.updatePartyStatus();
	}

	@Transactional
	public void executeParty(Long id, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.validateExecutable(party, actor);

		party.updateFinalStatus(PartyStatus.COMPLETED);
	}

	@Transactional
	public void unexecuteParty(Long id, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.validateExecutable(party, actor);

		party.updateFinalStatus(PartyStatus.CANCELLED);
	}
}
