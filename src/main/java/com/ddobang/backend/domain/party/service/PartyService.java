package com.ddobang.backend.domain.party.service;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.party.dto.PartyDto;
import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartyDetailResponse;
import com.ddobang.backend.domain.party.dto.response.PartyMainResponse;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.exception.PartyErrorCode;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.repository.PartyRepository;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.response.PageDto;
import com.ddobang.backend.global.response.SliceDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyService {
	private final PartyRepository partyRepository;
	private final ThemeService themeService;
	private final MemberService memberService;
	private final PartyValidationService partyValidationService;

	public List<PartyMainResponse> getUpcomingParties() {
		List<Party> parties = partyRepository.findTop12ByStatusOrderByScheduledAtAsc(PartyStatus.RECRUITING);
		return parties.stream().map(PartyMainResponse::from).collect(Collectors.toList());
	}


	public SliceDto<PartySummaryResponse> getParties(Long lastId, int size, PartySearchCondition partySearchCondition) {
		List<PartySummaryResponse> parties = partyRepository.getParties(lastId, size + 1, partySearchCondition);

		return SliceDto.of(parties, size);
	}

	public Party getPartyById(Long id) {
		return partyRepository.findById(id)
			.orElseThrow(() -> new PartyException(PartyErrorCode.PARTY_NOT_FOUND));
	}

	public PartyDetailResponse getPartyDetailResponse(Long id, Member actor) {
		Party party = getPartyById(id);
		ThemeStat themeStat = themeService.getThemeStatById(id);
		return PartyDetailResponse.from(party, themeStat, actor);
	}

	@Transactional
	public PartyDto createParty(PartyRequest request, Member actor) {
		Theme theme = themeService.getThemeById(request.themeId());
		Party party = Party.of(request, theme, actor);
		return PartyDto.from(partyRepository.save(party));
	}

	@Transactional
	public PartyDto modifyParty(Long id, PartyRequest request, Member actor) {
		Party party = getPartyById(id);
		partyValidationService.validateModifiable(party, actor);
		Theme theme = themeService.getThemeById(request.themeId());
		party.modifyParty(request, theme);
		return PartyDto.from(party);
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
		Member member = memberService.getMember(memberId);

		partyValidationService.validateAccept(party, member, actor);

		party.updatePartyMemberStatus(member, PartyMemberStatus.ACCEPTED);
		party.updatePartyStatus();
	}

	@Transactional
	public void executeParty(Long id, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.validateExecutable(party, actor);

		party.updateStatus(PartyStatus.COMPLETED);
	}

	@Transactional
	public void unexecuteParty(Long id, Member actor) {
		Party party = getPartyById(id);

		partyValidationService.validateExecutable(party, actor);

		party.updateStatus(PartyStatus.CANCELLED);
	}

	public PageDto<PartySummaryResponse> getOtherJoinedParties(Long memberId, int page, int size) {
		Member member = memberService.getMember(memberId);
		Pageable pageable = PageRequest.of(page, size);
		Page<PartySummaryResponse> joinedParties = partyRepository.findByMemberJoined(member, pageable, false);
		return PageDto.of(joinedParties);
	}

	public PageDto<PartySummaryResponse> getMyJoinedParties(Member actor, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<PartySummaryResponse> joinedParties = partyRepository.findByMemberJoined(actor, pageable, true);
		return PageDto.of(joinedParties);
	}
}
