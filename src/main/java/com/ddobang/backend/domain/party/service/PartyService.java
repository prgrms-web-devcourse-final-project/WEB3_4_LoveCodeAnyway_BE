package com.ddobang.backend.domain.party.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.member.service.MemberReviewService;
import com.ddobang.backend.domain.member.service.MemberService;
import com.ddobang.backend.domain.member.types.MemberReviewKeyword;
import com.ddobang.backend.domain.party.dto.PartyDto;
import com.ddobang.backend.domain.party.dto.request.PartyMemberReviewRequest;
import com.ddobang.backend.domain.party.dto.request.PartyRequest;
import com.ddobang.backend.domain.party.dto.request.PartySearchCondition;
import com.ddobang.backend.domain.party.dto.response.PartyDetailResponse;
import com.ddobang.backend.domain.party.dto.response.PartyMainResponse;
import com.ddobang.backend.domain.party.dto.response.PartySummaryResponse;
import com.ddobang.backend.domain.party.entity.Party;
import com.ddobang.backend.domain.party.entity.PartyMemberReview;
import com.ddobang.backend.domain.party.exception.PartyErrorCode;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.repository.PartyMemberReviewRepository;
import com.ddobang.backend.domain.party.repository.PartyRepository;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
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
	private final PartyMemberReviewRepository reviewRepository;
	private final MemberReviewService memberReviewService;

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

	@Transactional
	public void reviewAll(Long id, List<PartyMemberReviewRequest> requests, Member actor) {
		Party party = getPartyById(id);

		Set<Long> reviewedMemberIds = new HashSet<>();

		for (PartyMemberReviewRequest request : requests) {
			Member receiver = memberService.getMember(request.targetId());

			List<MemberReviewKeyword> keywords = request.reviewKeywords().stream()
				.map(MemberReviewKeyword::valueOf)
				.toList();

			PartyMemberReview review = PartyMemberReview.of(party, actor, receiver);
			for (MemberReviewKeyword keyword : keywords) {
				review.addKeyword(keyword);
			}

			reviewRepository.save(review);

			reviewedMemberIds.add(receiver.getId());
		}

		for (Long memberId : reviewedMemberIds) {
			memberReviewService.updateMemberReview(memberId);
		}
	}
}
