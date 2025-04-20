package com.ddobang.backend.domain.party.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.ddobang.backend.domain.party.entity.PartyMember;
import com.ddobang.backend.domain.party.entity.PartyMemberReview;
import com.ddobang.backend.domain.party.event.PartyApplyEvent;
import com.ddobang.backend.domain.party.event.PartyMemberStatusUpdatedEvent;
import com.ddobang.backend.domain.party.exception.PartyErrorCode;
import com.ddobang.backend.domain.party.exception.PartyException;
import com.ddobang.backend.domain.party.repository.PartyMemberRepository;
import com.ddobang.backend.domain.party.repository.PartyMemberReviewRepository;
import com.ddobang.backend.domain.party.repository.PartyRepository;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import com.ddobang.backend.domain.party.types.PartyStatus;
import com.ddobang.backend.domain.theme.entity.Theme;
import com.ddobang.backend.domain.theme.entity.ThemeStat;
import com.ddobang.backend.domain.theme.service.ThemeService;
import com.ddobang.backend.global.event.EventPublisher;
import com.ddobang.backend.global.response.PageDto;
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
	private final PartyMemberRepository partyMemberRepository;

	//추가 이벤트 퍼블리셔
	private final EventPublisher eventPublisher;

	public List<PartyMainResponse> getUpcomingParties() {
		List<Party> parties = partyRepository.findTop12ByStatusOrderByScheduledAtAsc(PartyStatus.RECRUITING);
		return parties.stream().map(PartyMainResponse::from).collect(Collectors.toList());
	}

	public SliceDto<PartySummaryResponse> getParties(Long lastId, int size, PartySearchCondition partySearchCondition) {
		List<PartySummaryResponse> parties = partyRepository.getParties(lastId, size + 1, partySearchCondition);

		return SliceDto.of(parties, size);
	}

	public SliceDto<PartySummaryResponse> getPartiesByTheme(Long themeId, Long lastId, int size) {
		Theme theme = themeService.getThemeById(themeId);
		List<PartySummaryResponse> parties = partyRepository.getPartiesByTheme(theme, lastId, size + 1);
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
		Party party = partyRepository.save(Party.of(request, theme));
		PartyMember host = partyMemberRepository.save(PartyMember.createHost(party, actor));
		party.addPartyMember(host);
		return PartyDto.from(party);
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

		// boolean isNewApplication = !party.isPartyMember(actor); // 추가: 신청자 여부 확인

		if (party.isPartyMember(actor)) {
			party.updatePartyMemberStatus(actor, PartyMemberStatus.APPLICANT);
		} else {
			PartyMember applicant = PartyMember.of(party, actor);
			party.addPartyMember(applicant);
			partyMemberRepository.save(applicant);
		}

		// 모임장이 아닌 경우(=신청자)만 이벤트 발행
		Member host = party.getHost();
		if (!host.getId().equals(actor.getId())) {
			// 모임 신청 이벤트 발행
			eventPublisher.publish(PartyApplyEvent.builder()
				.partyId(party.getId())
				.partyTitle(party.getTitle())
				.hostId(host.getId())
				.applicantId(actor.getId())
				.applicantNickname(actor.getNickname())
				.build());
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

		// 추가: 상태 변경 이벤트 발행
		eventPublisher.publish(PartyMemberStatusUpdatedEvent.builder()
			.partyId(party.getId())
			.partyTitle(party.getTitle())
			.memberId(member.getId())  // 알림 수신자 (신청자)
			.hostId(actor.getId())
			.hostNickname(actor.getNickname())
			.newStatus(PartyMemberStatus.ACCEPTED)
			.build());
	}

	// 거절 처리 (취소 상태로 변경)
	@Transactional
	public void rejectPartyMember(Long id, Long memberId, Member actor) {
		Party party = getPartyById(id);
		Member member = memberService.getMember(memberId);

		partyValidationService.validateReject(party, member, actor);

		// 거절 상태로 변경
		party.updatePartyMemberStatus(member, PartyMemberStatus.REJECTED);
		party.updatePartyStatus();

		// 상태 변경 이벤트 발행
		eventPublisher.publish(PartyMemberStatusUpdatedEvent.builder()
			.partyId(party.getId())
			.partyTitle(party.getTitle())
			.memberId(member.getId())  // 알림 수신자 (신청자)
			.hostId(actor.getId())
			.hostNickname(actor.getNickname())
			.newStatus(PartyMemberStatus.REJECTED)
			.build());
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
